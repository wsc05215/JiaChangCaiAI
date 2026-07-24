package com.zzx.jiachangcai.service.impl;

import com.zzx.jiachangcai.entity.User;
import com.zzx.jiachangcai.mapper.UserMapper;
import com.zzx.jiachangcai.service1.UserService1;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.List;

@Service
public class UserServiceImpl implements UserService1 {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> getAllUser() {
        return userMapper.selectAllUser();
    }
}

