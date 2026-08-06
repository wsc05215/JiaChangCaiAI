package com.jcx.jiachangcai.module.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.ai.entity.CustomRecord;
import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.mapper.CustomRecordMapper;
import com.jcx.jiachangcai.module.ai.service.ICustomRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CustomRecordServiceImpl extends ServiceImpl<CustomRecordMapper, CustomRecord> implements ICustomRecordService {

    @Override
    public void saveRecord(Long userId, AiChatType type, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        CustomRecord record = new CustomRecord();
        record.setUserId(userId);
        record.setType(type.name());
        record.setTitle(extractTitle(content, type));
        record.setContent(content);
        record.setCreateTime(LocalDateTime.now());
        save(record);
    }

    @Override
    public List<CustomRecord> listByUserIdAndType(Long userId, AiChatType type) {
        LambdaQueryWrapper<CustomRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomRecord::getUserId, userId)
                .eq(CustomRecord::getType, type.name())
                .orderByDesc(CustomRecord::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<CustomRecord> listByUserId(Long userId) {
        LambdaQueryWrapper<CustomRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomRecord::getUserId, userId)
                .orderByDesc(CustomRecord::getCreateTime);
        return list(wrapper);
    }

    private String extractTitle(String content, AiChatType type) {
        if (content == null || content.isBlank()) {
            return type.getDisplayName() + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
        // 取第一行非空内容作为标题，限制长度
        String firstLine = content.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#") && !line.startsWith("-") && !line.startsWith("*"))
                .findFirst()
                .orElse("");
        if (firstLine.length() > 50) {
            firstLine = firstLine.substring(0, 50) + "...";
        }
        if (firstLine.isEmpty()) {
            firstLine = type.getDisplayName() + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
        return firstLine;
    }
}
