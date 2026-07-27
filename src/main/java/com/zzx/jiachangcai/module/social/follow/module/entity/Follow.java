package com.zzx.jiachangcai.module.social.follow.module.entity;

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
 * 用户关注表
 * </p>
 *
 * @author wsc
 * @since 2026-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("follow")
@Schema(description = "用户关注表")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description ="主键")
    @TableId(value = "follow_id", type = IdType.AUTO)
    private Long followId;

    @Schema(description ="关注者用户ID")
    private Long followerId;

    @Schema(description ="被关注者用户ID")
    private Long followeeId;

    @Schema(description ="关注时间")
    private LocalDateTime createTime;


}
