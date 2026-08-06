package com.jcx.jiachangcai.module.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcx.jiachangcai.module.ai.entity.CustomRecord;
import com.jcx.jiachangcai.module.ai.enums.AiChatType;

import java.util.List;

public interface ICustomRecordService extends IService<CustomRecord> {
    void saveRecord(Long userId, AiChatType type, String content);
    List<CustomRecord> listByUserIdAndType(Long userId, AiChatType type);
    List<CustomRecord> listByUserId(Long userId);
}
