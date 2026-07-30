package com.jcx.jiachangcai.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.user.entity.User;
import com.jcx.jiachangcai.module.user.mapper.UserMapper;
import com.jcx.jiachangcai.module.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
}
