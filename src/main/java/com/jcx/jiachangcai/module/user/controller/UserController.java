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
    // 登录
    @PostMapping("/login")
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        return service.login(username, password);
    }

    // 我的详情（头像+昵称）
    @GetMapping("/mydetail")
    public User details(Long id) {
        if (id == null) {
            return null;
        }
        return service.getdetail(id);
    }


    //查询我的——>作品数
    @GetMapping("/works")
    public Integer wordks(Long id){
       Integer count = recipeService.getcountByid(id);
       return count;
    }

    //查询我的->>获赞数
    @GetMapping("/likeCount")
    public Integer likeCount(Long id){
        Integer likeCount = recipeService.getLikecount(id);
        return likeCount;
    }
}
