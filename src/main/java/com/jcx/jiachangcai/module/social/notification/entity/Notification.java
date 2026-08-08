package com.jcx.jiachangcai.module.social.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notification")
@Schema(description = "消息通知表")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "接收通知的用户ID")
    private Long userId;

    @Schema(description = "通知类型（comment_like / follow 等）")
    private String type;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "关联的评论ID")
    private Long commentId;

    @Schema(description = "关联的菜谱ID")
    private Long recipeId;

    @Schema(description = "触发通知的用户ID（点赞者）")
    private Long fromUserId;

    @Schema(description = "是否已读：0未读 1已读")
    private Integer isRead;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
