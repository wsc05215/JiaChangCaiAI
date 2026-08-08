package com.jcx.jiachangcai.module.social.comment.service;

import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.social.comment.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author wsc
 * @since 2026-08-01
 */
public interface ICommentService extends IService<Comment> {
        List<Comment> getCommentOfRecipe(Long recipe_id);
        void addComment(Comment comment);
        Long getCountOfRecipe(Recipe recipe);
}
