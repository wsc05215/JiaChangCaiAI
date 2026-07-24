package com.zzx.jiachangcai.module.user.service.impl;

import com.zzx.jiachangcai.module.user.entity.User;
import com.zzx.jiachangcai.module.user.mapper.UserMapper;
import com.zzx.jiachangcai.module.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
