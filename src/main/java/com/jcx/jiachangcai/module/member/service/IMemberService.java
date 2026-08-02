package com.jcx.jiachangcai.module.member.service;

import com.jcx.jiachangcai.module.member.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;

import java.sql.Time;
import java.time.LocalDateTime;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-07-30
 */
public interface IMemberService extends IService<Member> {
    boolean getisMember(Long id);
    void addMember(Long user_id, Integer memver_type);
    LocalDateTime viewExperTime(Long user_Id);
}
