package com.zzx.jiachangcai.module.social.follow.module.controller;


import com.zzx.jiachangcai.module.social.follow.module.service.IFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
    //查看当前关注数
    @GetMapping("/followingCount")
    public Long followingCount(Long id){
       Long followingCount = service.getFollowingCount(id);
       return followingCount;
    }

    //查看当前粉丝数
    @GetMapping("/followeeCount")
    public Long followeeCount(Long id){
        Long followerCount = service.gettFollowerCount(id);
        return followerCount;
    }
}
