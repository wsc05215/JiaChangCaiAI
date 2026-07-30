package com.jcx.jiachangcai.module.member.controller;


import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-07-30
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private IMemberService service;

    @GetMapping("/check")
    public boolean checkMember(@RequestParam Long userId) {
        return service.getisMember(userId);
    }
}
