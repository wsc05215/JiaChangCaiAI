package com.zzx.jiachangcai.module.social.follow.module.mapper;

import com.zzx.jiachangcai.module.social.follow.module.entity.Follow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户关注表 Mapper 接口
 * </p>
 *
 * @author wsc
 * @since 2026-07-27
 */
public interface FollowMapper extends BaseMapper<Follow> {
    Long selectFollowingCount(Long id);
    Long selectFollowerCount(Long id);
}
