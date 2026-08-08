package com.jcx.jiachangcai.module.social.comment.entity;

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
@TableName("comment_like")
@Schema(description = "评论点赞记录")
public class CommentLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "点赞记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "点赞用户ID")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
