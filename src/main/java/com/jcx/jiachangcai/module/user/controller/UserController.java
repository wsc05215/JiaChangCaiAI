package com.jcx.jiachangcai.module.user.controller;

import com.jcx.jiachangcai.module.recipe.service.IRecipeService;
import com.jcx.jiachangcai.module.user.entity.User;
import com.jcx.jiachangcai.module.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    private IRecipeService recipeService;

    // 密码登录
    @PostMapping("/login")
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        return service.login(username, password);
    }

    // 发送邮箱验证码
    @PostMapping("/sendCode")
    public String sendCode(String email) {
        if (email == null || !email.contains("@")) {
            return "fail";
        }
        try {
            service.sendCode(email);
            return "ok";
        } catch (Exception e) {
            return "fail";
        }
    }

    // 邮箱验证码登录
    @PostMapping("/emailLogin")
    public User emailLogin(String email, String code) {
        if (email == null || email.isEmpty() || code == null || code.isEmpty()) {
            return null;
        }
        return service.emailLogin(email, code);
    }

    // 我的详情（头像+昵称）
    @GetMapping("/mydetail")
    public User details(Long id) {
        if (id == null) {
            return null;
        }
        return service.getdetail(id);
    }

    // 查询我的——>作品数
    @GetMapping("/works")
    public Integer wordks(Long id) {
        Integer count = recipeService.getcountByid(id);
        return count;
    }

    // 查询我的->>获赞数
    @GetMapping("/likeCount")
    public Integer likeCount(Long id) {
        Integer likeCount = recipeService.getLikecount(id);
        return likeCount;
    }

    // 检查是否首次登录（密码为空=自动注册用户）
    @GetMapping("/isFirstLogin")
    public boolean isFirstLogin(Long userId) {
        return service.isFirstLogin(userId);
    }

    //更改用户信息
    @PostMapping("/alterUser")
    public String alterUser(Long userId, String username, String password, String nickName, String phone, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setNickName(nickName);
        user.setPhone(phone);
        user.setEmail(email);
        service.alterUserDeail(user);
        return "ok";
    }
}
