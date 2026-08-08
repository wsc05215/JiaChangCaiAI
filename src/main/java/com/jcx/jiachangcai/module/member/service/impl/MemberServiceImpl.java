package com.jcx.jiachangcai.module.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.member.entity.Member;
import com.jcx.jiachangcai.module.member.mapper.MemberMapper;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public void addMember(Long user_id, Integer member_type) {
        if (!getisMember(user_id)) {
            LocalDateTime now = LocalDateTime.now();
            Member member = new Member();
            member.setUserId(user_id);
            member.setMemberType(member_type);
            member.setStatus(1);
            member.setStartTime(now);
            if (member_type == 1) {
                member.setExpireTime(now.plusHours(2));
                member.setMemberLevel("食谱定制会员");
                member.setAmount(new BigDecimal("5.9"));
            } else if (member_type == 2) {
                member.setExpireTime(now.plusMonths(1));
                member.setMemberLevel("食材管理会员");
                member.setAmount(new BigDecimal("18"));
            } else if (member_type == 3) {
                member.setExpireTime(now.plusYears(1));
                member.setMemberLevel("尊享会员");
                member.setAmount(new BigDecimal("128"));
            }
            member.setMealCount(0);
            member.setIsDeleted(0);
            member.setCreateTime(now);
            member.setUpdateTime(now);
            mapper.insert(member);
        }
    }

    @Override
    public LocalDateTime viewExperTime(Long user_Id) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getUserId, user_Id)
               .eq(Member::getStatus, 1)
               .gt(Member::getExpireTime, LocalDateTime.now())
               .orderByDesc(Member::getExpireTime)
               .last("LIMIT 1");
        Member member = mapper.selectOne(wrapper);
        return member != null ? member.getExpireTime() : null;
    }

    @Override
    public Member getActiveMember(Long userId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getUserId, userId)
               .eq(Member::getStatus, 1)
               .gt(Member::getExpireTime, LocalDateTime.now())
               .orderByDesc(Member::getExpireTime)
               .last("LIMIT 1");
        return mapper.selectOne(wrapper);
    }

}
