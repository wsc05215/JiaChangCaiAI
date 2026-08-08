package com.jcx.jiachangcai.module.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("custom_record")
public class CustomRecord implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String description;
    private String cookTime;
    private String difficulty;
    private String ingredients;
    private String steps;
    private String content;
    private LocalDateTime createTime;
}
