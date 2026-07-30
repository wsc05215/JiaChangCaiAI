package com.jcx.jiachangcai.module.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.member.entity.Member;
import com.jcx.jiachangcai.module.member.mapper.MemberMapper;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-07-30
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {
    @Autowired
    private MemberMapper mapper;

    @Override
    public boolean getisMember(Long userId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getUserId, userId)
               .eq(Member::getStatus, 1)
               .gt(Member::getExpireTime, LocalDateTime.now());
        return mapper.selectCount(wrapper) > 0;
    }
}
