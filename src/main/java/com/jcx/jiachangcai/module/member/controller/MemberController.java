package com.jcx.jiachangcai.module.member.controller;


import com.jcx.jiachangcai.module.member.entity.Member;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private IMemberService service;

    @GetMapping("/check")
    public boolean checkMember(@RequestParam Long userId) {
        return service.getisMember(userId);
    }


    @GetMapping("/addMember")
    public String addMember(@RequestParam Long user_id,@RequestParam Integer member_type){
        service.addMember(user_id,member_type);
        return "ok";
    }

    //显示会员到期时间
    @GetMapping("/viewExperTime")
    public LocalDateTime viewExperTime(Long user_id){
      return   service.viewExperTime(user_id);
    }

    //获取会员详情（等级、类型、到期时间）
    @GetMapping("/info")
    public Map<String, Object> memberInfo(@RequestParam Long userId) {
        Member member = service.getActiveMember(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        if (member == null) {
            result.put("level", "非会员");
            result.put("memberType", 0);
            result.put("expireTime", null);
        } else {
            result.put("level", member.getMemberLevel());
            result.put("memberType", member.getMemberType());
            result.put("expireTime", member.getExpireTime());
        }
        return result;
    }
}
