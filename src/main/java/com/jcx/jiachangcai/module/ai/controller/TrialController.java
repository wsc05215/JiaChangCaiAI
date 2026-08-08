package com.jcx.jiachangcai.module.ai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.ai.entity.TrialUsage;
import com.jcx.jiachangcai.module.ai.mapper.TrialUsageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trial")
public class TrialController {

    @Autowired
    private TrialUsageMapper trialUsageMapper;

    /**
     * 查询用户已试用过的 AI 功能列表
     * 返回 {"CustomizedRecipe": true, "Oneclickmenu": false, ...}
     */
    @GetMapping("/check")
    public Map<String, Boolean> checkTrial(@RequestParam Long userId) {
        List<TrialUsage> records = trialUsageMapper.selectList(
                new LambdaQueryWrapper<TrialUsage>().eq(TrialUsage::getUserId, userId)
        );
        List<String> tried = records.stream()
                .map(TrialUsage::getAiType)
                .collect(Collectors.toList());

        Map<String, Boolean> result = new HashMap<>();
        result.put("CustomizedRecipe", tried.contains("CustomizedRecipe"));
        result.put("Oneclickmenu", tried.contains("Oneclickmenu"));
        result.put("AiFridgeFoodService", tried.contains("AiFridgeFoodService"));
        return result;
    }
}
