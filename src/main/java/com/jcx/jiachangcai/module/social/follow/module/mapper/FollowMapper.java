package com.jcx.jiachangcai.module.social.follow.module.mapper;

import com.jcx.jiachangcai.module.social.follow.module.entity.Follow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcx.jiachangcai.module.user.entity.User;

import java.util.List;

public interface FollowMapper extends BaseMapper<Follow> {
    List<User> getFollowing(Long followerId);
    List<User> getFollowers(Long followeeId);
}
