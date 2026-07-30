package com.jcx.jiachangcai.module.member.service.impl;

import com.jcx.jiachangcai.module.member.entity.Member;
import com.jcx.jiachangcai.module.member.mapper.MemberMapper;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
