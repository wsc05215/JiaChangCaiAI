package com.zzx.jiachangcai.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    private String nickName;
    private String avatar;
    private String phone;
    private Integer memberType;
    private LocalDateTime memberExpire;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
