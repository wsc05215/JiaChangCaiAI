package com.jcx.jiachangcai.module.user.mapper;

import com.jcx.jiachangcai.module.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wsc
 * @since 2026-07-24
 */
public interface UserMapper extends BaseMapper<User> {
     User selectdetail(Long id);
}
