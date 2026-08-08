package com.jcx.jiachangcai.module.social.follow.module.controller;


import com.jcx.jiachangcai.module.social.follow.module.service.IFollowService;
import com.jcx.jiachangcai.module.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户关注表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-07-27
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private IFollowService service;

    // 查看当前关注数
    @GetMapping("/followingCount")
    public Long followingCount(Long id) {
        return service.getFollowingCount(id);
    }

    // 查看当前粉丝数
    @GetMapping("/followeeCount")
    public Long followeeCount(Long id) {
        return service.gettFollowerCount(id);
    }

    // 关注
    @PostMapping("/add")
    public boolean addFollow(Long followerId, Long followeeId) {
        return service.follow(followerId, followeeId);
    }

    // 取消关注
    @DeleteMapping("/remove")
    public boolean removeFollow(Long followerId, Long followeeId) {
        return service.unfollow(followerId, followeeId);
    }

    // 检查是否已关注
    @GetMapping("/check")
    public boolean isFollowing(Long followerId, Long followeeId) {
        return service.isFollowing(followerId, followeeId);
    }



    //查看个人关注的博主
    @GetMapping("/getFollowing")
    public List<User> getFollowing(Long followerId){
        return service.getFollowing(followerId);
    }

    //查看粉丝
    @GetMapping("/getFollowers")
    public List<User> getFollowers(Long followeeId){
        return service.getFollowers(followeeId);
    }
}
