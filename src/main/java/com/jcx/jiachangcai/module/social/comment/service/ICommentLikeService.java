package com.jcx.jiachangcai.module.social.comment.service;

import com.jcx.jiachangcai.module.social.comment.entity.CommentLike;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ICommentLikeService extends IService<CommentLike> {
    String toggleLike(Long commentId, Long userId);
    boolean hasLiked(Long commentId, Long userId);
}
