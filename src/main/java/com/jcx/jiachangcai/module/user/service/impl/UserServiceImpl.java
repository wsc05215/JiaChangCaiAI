package com.jcx.jiachangcai.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.user.entity.User;
import com.jcx.jiachangcai.module.user.mapper.UserMapper;
import com.jcx.jiachangcai.module.user.service.CodeManager;
import com.jcx.jiachangcai.module.user.service.EmailService;
import com.jcx.jiachangcai.module.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-24
 */
@Service
@Primary
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private EmailService emailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public User login(String account, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, account)
               .or()
               .eq(User::getPhone, account);
        User user = userMapper.selectOne(wrapper);

        if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
            return null;
        }
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User getdetail(Long id) {
        return userMapper.selectdetail(id);
    }

    @Override
    public void sendCode(String email) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        codeManager.save(email, code);
        emailService.sendCode(email, code);
    }

    @Override
    public User emailLogin(String email, String code) {
        log.info("邮箱登录: email={}, code={}", email, code);
        if (!codeManager.verify(email, code)) {
            log.warn("验证码校验失败: email={}, code={}", email, code);
            return null;
        }

        // 查已有用户（username 或 email 匹配）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, email).or().eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);

        if (user != null) {
            if (Integer.valueOf(1).equals(user.getIsDeleted())) {
                log.warn("用户已注销: email={}", email);
                return null;
            }
            log.info("用户已存在，直接返回: userId={}", user.getUserId());
            return user;
        }

        log.info("用户不存在，准备自动注册: email={}", email);
        // 防止并发：再次检查用户是否在此期间被创建
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, email).or().eq(User::getEmail, email);
        user = userMapper.selectOne(wrapper);
        if (user != null) {
            if (Integer.valueOf(1).equals(user.getIsDeleted())) {
                log.warn("二次检查-用户已注销: email={}", email);
                return null;
            }
            log.info("二次检查-用户已存在: userId={}", user.getUserId());
            return user;
        }

        // 新用户自动注册
        log.info("开始自动注册新用户: email={}", email);
        String nick = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        user = new User();
        user.setUsername(email);
        user.setNickName(nick);
        user.setEmail(email);
        user.setPassword("");
        user.setMemberType(0);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 尝试插入用户，如果并发插入导致冲突则查询已存在的用户
        try {
            userMapper.insert(user);
            log.info("新用户插入成功: userId={}", user.getUserId());
            // 获取自动生成的ID
            return userMapper.selectById(user.getUserId());
        } catch (Exception e) {
            log.error("新用户插入失败: email={}, error={}", email, e.getMessage(), e);
            // 如果插入失败（可能是并发情况），重新查询用户
            LambdaQueryWrapper<User> finalWrapper = new LambdaQueryWrapper<>();
            finalWrapper.eq(User::getUsername, email).or().eq(User::getEmail, email);
            User existingUser = userMapper.selectOne(finalWrapper);
            if (existingUser != null && !Integer.valueOf(1).equals(existingUser.getIsDeleted())) {
                return existingUser;
            }
            return null;
        }
    }

    @Override
    public void alterUserDeail(User user) {
        // 如果密码为空，保留原密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            User existing = userMapper.selectById(user.getUserId());
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        }
        userMapper.alterUser(user);
    }

    @Override
    public boolean isFirstLogin(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && (user.getPassword() == null || user.getPassword().isEmpty());
    }
//查询邮箱是否存在
    @Override
    public String findEmail(String email) {
        LambdaQueryWrapper<User> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,email);
       User user =  userMapper.selectOne(wrapper);
       if(user==null){
           return "该用户不存在";
       }else{
           sendCode(email);
           return "ok";
       }
    }
//发送验证码 重置密码
    @Override
    public String reastPassword(String email, String code, String newPassword) {
        if (!codeManager.verify(email, code)) {
            return "验证码已经过期";
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return "该用户不存在";
        }
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return "更改成功";
    }

    @Override
    public String deleUser(Long user_id) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, user_id);
        userMapper.delete(wrapper);
        return "OK";
    }

    @Override
    public String addUser(String email, String username, String password) {
        // 检查用户名是否已被注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectOne(wrapper) != null) {
            return "用户名已被注册";
        }

        // 检查邮箱是否已被注册
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (userMapper.selectOne(wrapper) != null) {
            return "邮箱已被注册";
        }

        String nick = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setNickName(nick);
        newUser.setMemberType(0);
        newUser.setIsDeleted(0);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        try {
            userMapper.insert(newUser);
            return "ok";
        } catch (Exception e) {
            // 并发插入冲突，返回友好提示
            return "用户已经被注册";
        }
    }
}

