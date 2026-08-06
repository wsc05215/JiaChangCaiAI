package com.jcx.jiachangcai.module.user.service;

import com.jcx.jiachangcai.module.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-07-24
 */
public interface IUserService extends IService<User> {
    User login(String account, String password);
    User getdetail(Long id);
    void sendCode(String email);
    User emailLogin(String email, String code);
    void alterUserDeail(User user);
    boolean isFirstLogin(Long userId);
    String findEmail(String email);
    String reastPassword(String email,String code,String newPassword);
    String deleUser(Long user_id);
    String addUser(String email, String username, String password);
}
