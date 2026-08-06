package com.jcx.jiachangcai.module.user.controller;

import com.jcx.jiachangcai.module.recipe.service.IRecipeService;
import com.jcx.jiachangcai.module.user.entity.User;
import com.jcx.jiachangcai.module.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
            log.error("发送验证码失败: email={}", email, e);
            return "fail:" + e.getMessage();
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

//查询邮箱是否被注册
    @PostMapping("/findEmail")
    public String findEmail(String email) {
        String msg = service.findEmail(email);
        return msg;
    }

    //根据邮箱验证码重置密码
    @PostMapping("/reastPassword")
    public String restPassword(String email, String code, String newPassword){
        if(email==null||code==null||newPassword==null){
            return "null";
        }else{
           String msg = service.reastPassword(email,code,newPassword);
           return msg;
        }
    }

    //注销账号
    @DeleteMapping("/deleUser")
    public String deleUser(Long user_id){
        service.deleUser(user_id);
        return "ok";
    }

    //注册新账号
    @PostMapping("/addUser")
    public String addUser(String email,String username,String password){
      return   service.addUser(email,username,password);
    }

}
