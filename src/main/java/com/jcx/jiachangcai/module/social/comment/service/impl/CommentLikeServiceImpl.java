package com.jcx.jiachangcai.module.social.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.social.comment.entity.Comment;
import com.jcx.jiachangcai.module.social.comment.entity.CommentLike;
import com.jcx.jiachangcai.module.social.comment.mapper.CommentLikeMapper;
import com.jcx.jiachangcai.module.social.comment.mapper.CommentMapper;
import com.jcx.jiachangcai.module.social.comment.service.ICommentLikeService;
import com.jcx.jiachangcai.module.social.notification.entity.Notification;
import com.jcx.jiachangcai.module.social.notification.mapper.NotificationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLike> implements ICommentLikeService {

    @Autowired
    private CommentLikeMapper mapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    @Override
    public String toggleLike(Long commentId, Long userId) {
        // 查询是否已点赞
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        CommentLike exist = mapper.selectOne(wrapper);

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return "评论不存在";
        }

        if (exist != null) {
            // 已点赞 → 取消点赞
            mapper.deleteById(exist.getId());
            // likeCount 减 1
            if (comment.getLikeCount() != null && comment.getLikeCount() > 0) {
                comment.setLikeCount(comment.getLikeCount() - 1);
                commentMapper.updateById(comment);
            }
            return "取消点赞";
        } else {
            // 未点赞 → 点赞
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            mapper.insert(like);
            // likeCount 加 1
            comment.setLikeCount(comment.getLikeCount() != null ? comment.getLikeCount() + 1 : 1);
            commentMapper.updateById(comment);

            // 如果赞的不是自己的评论，发通知给评论作者
            if (!userId.equals(comment.getUserId())) {
                Notification noti = new Notification();
                noti.setUserId(comment.getUserId());     // 评论作者收到通知
                noti.setType("comment_like");
                noti.setContent("赞了你的评论");
                noti.setCommentId(commentId);
                noti.setRecipeId(comment.getRecipeId());
                noti.setFromUserId(userId);
                noti.setIsRead(0);
                noti.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(noti);
            }
            return "点赞成功";
        }
    }

    @Override
    public boolean hasLiked(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        return mapper.selectCount(wrapper) > 0;
    }
}
