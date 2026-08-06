package com.jcx.jiachangcai.module.ai.controller;

import com.jcx.jiachangcai.module.ai.entity.CustomRecord;
import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.service.ICustomRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/custom")
public class CustomRecordController {

    @Autowired
    private ICustomRecordService customRecordService;

    @GetMapping("/list")
    public List<CustomRecord> list(@RequestParam Long userId,
                                   @RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) {
            try {
                AiChatType chatType = AiChatType.valueOf(type);
                return customRecordService.listByUserIdAndType(userId, chatType);
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
        return customRecordService.listByUserId(userId);
    }

    @GetMapping("/detail/{id}")
    public CustomRecord detail(@PathVariable Long id) {
        return customRecordService.getById(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        customRecordService.removeById(id);
        return "ok";
    }
}
