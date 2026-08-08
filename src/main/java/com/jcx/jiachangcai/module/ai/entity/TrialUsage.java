package com.jcx.jiachangcai.module.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 非会员试用记录表
 */
@Data
@TableName("trial_usage")
public class TrialUsage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 对应 AiChatType 枚举名：CustomizedRecipe / Oneclickmenu / AiFridgeFoodService */
    private String aiType;

    private LocalDateTime createTime;
}
