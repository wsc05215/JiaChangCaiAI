package com.zzx.jiachangcai.module.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzx.jiachangcai.module.user.entity.User;
import com.zzx.jiachangcai.module.user.mapper.UserMapper;
import com.zzx.jiachangcai.module.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-07-24
 */
@RestController
@RequestMapping("/user")
public class UserController {
@Autowired
private IUserService service;
@Autowired
private UserMapper userMapper;
    //登录
    @PostMapping("/Userlogin")
    public String Userlogin(String username,String password){
        //判断用户输入的是否为空
        if(username == null || username.isEmpty() || password == null || password.isEmpty()){
            return "error";
        }
        LambdaQueryWrapper<User> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username).or().eq(User::getPhone,username);
        User user = userMapper.selectOne(wrapper);

        if(user == null || user.getIsDeleted() == 1){
            return "error";
        }
        if(user.getPassword().equals(password)){
            return "ok";
        }else{
            return "error";
        }
    }


}
