package com.jcx.jiachangcai.module.social.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jcx.jiachangcai.module.recipe.entity.Recipe;
import com.jcx.jiachangcai.module.recipe.mapper.RecipeMapper;
import com.jcx.jiachangcai.module.social.comment.entity.Comment;
import com.jcx.jiachangcai.module.social.comment.mapper.CommentMapper;
import com.jcx.jiachangcai.module.social.comment.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author wsc
 * @since 2026-08-01
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private CommentMapper mapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Override
    public List<Comment> getCommentOfRecipe(Long recipe_id) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getRecipeId, recipe_id)
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime);
        return mapper.selectList(wrapper);
    }

    @Override
    public void addComment(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setIsDeleted(0);
        mapper.insert(comment);
        // 同步更新菜谱评论数
        LambdaUpdateWrapper<Recipe> uw = new LambdaUpdateWrapper<>();
        uw.eq(Recipe::getRecipeId, comment.getRecipeId())
          .setSql("comment_count = COALESCE(comment_count, 0) + 1");
        recipeMapper.update(null, uw);
    }

    //查看菜谱评论数量
    @Override
    public Long getCountOfRecipe(Recipe recipe) {
       Long recipeid =  recipe.getRecipeId();
       LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(Comment::getRecipeId, recipeid)
              .eq(Comment::getIsDeleted, 0);
       return mapper.selectCount(wrapper);
    }


}
