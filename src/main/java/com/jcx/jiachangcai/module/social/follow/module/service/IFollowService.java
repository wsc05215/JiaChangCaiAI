package com.jcx.jiachangcai.module.social.follow.module.service;

import com.jcx.jiachangcai.module.social.follow.module.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jcx.jiachangcai.module.user.entity.User;

import java.util.List;

/**
 * <p>
 * 用户关注表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-07-27
 */
public interface IFollowService extends IService<Follow> {
    Long getFollowingCount(Long id);
    Long gettFollowerCount(Long id);
    boolean follow(Long followerId, Long followeeId);
    boolean unfollow(Long followerId, Long followeeId);
    boolean isFollowing(Long followerId, Long followeeId);
    List<User> getFollowing(Long Follower_id);
    List<User> getFollowers(Long followeeId);
}
