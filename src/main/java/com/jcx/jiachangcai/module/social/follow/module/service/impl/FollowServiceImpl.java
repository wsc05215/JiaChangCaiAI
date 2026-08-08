package com.jcx.jiachangcai.module.social.follow.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.social.follow.module.entity.Follow;
import com.jcx.jiachangcai.module.social.follow.module.mapper.FollowMapper;
import com.jcx.jiachangcai.module.social.follow.module.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Autowired
    private FollowMapper mapper;

    @Override
    public Long getFollowingCount(Long id) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, id);
        return count(wrapper);
    }

    @Override
    public Long gettFollowerCount(Long id) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFolloweeId, id);
        return count(wrapper);
    }

    @Override
    public boolean follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            return false;
        }
        if (isFollowing(followerId, followeeId)) {
            return false;
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFolloweeId(followeeId);
        return save(follow);
    }

    @Override
    public boolean unfollow(Long followerId, Long followeeId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId)
               .eq(Follow::getFolloweeId, followeeId);
        return remove(wrapper);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followeeId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId)
               .eq(Follow::getFolloweeId, followeeId);
        return count(wrapper) > 0;
    }

    @Override
    public List<User> getFollowing(Long followerId) {
        return mapper.getFollowing(followerId);
    }

    @Override
    public List<User> getFollowers(Long followeeId) {
        return mapper.getFollowers(followeeId);
    }
}
