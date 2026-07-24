package com.zzx.jiachangcai.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wsc
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@Schema(description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description ="用户唯一主键")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @Schema(description ="登录用户名")
    private String username;

    @Schema(description ="加密密码")
    private String password;

    @Schema(description ="用户昵称")
    private String nickName;

    @Schema(description ="头像OSS地址")
    private String avatar;

    @Schema(description ="手机号")
    private String phone;

    @Schema(description ="会员类型 0普通 1月卡 2年卡")
    private Integer memberType;

    @Schema(description ="会员到期时间，null=非会员")
    private LocalDateTime memberExpire;

    @Schema(description ="逻辑删除 0正常 1已注销")
    private Integer isDeleted;

    @Schema(description ="账号创建时间")
    private LocalDateTime createTime;

    @Schema(description ="信息更新时间")
    private LocalDateTime updateTime;


}
