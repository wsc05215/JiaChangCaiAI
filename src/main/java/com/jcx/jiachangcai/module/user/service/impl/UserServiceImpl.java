package com.jcx.jiachangcai.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.user.entity.User;
import com.jcx.jiachangcai.module.user.mapper.UserMapper;
import com.jcx.jiachangcai.module.user.service.CodeManager;
import com.jcx.jiachangcai.module.user.service.EmailService;
import com.jcx.jiachangcai.module.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
        if (!codeManager.verify(email, code)) {
            return null;
        }

        // 查已有用户（username 或 email 匹配）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, email).or().eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);

        if (user != null) {
            if (Integer.valueOf(1).equals(user.getIsDeleted())) {
                return null;
            }
            return user;
        }

        // 新用户自动注册
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
        userMapper.insert(user);
        return user;
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

}
