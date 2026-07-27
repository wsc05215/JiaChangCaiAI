package com.zzx.jiachangcai.module.social.follow.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzx.jiachangcai.module.social.follow.module.entity.Follow;
import com.zzx.jiachangcai.module.social.follow.module.mapper.FollowMapper;
import com.zzx.jiachangcai.module.social.follow.module.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-27
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

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

}
