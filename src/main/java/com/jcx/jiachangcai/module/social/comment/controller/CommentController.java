package com.jcx.jiachangcai.module.social.comment.controller;


import com.jcx.jiachangcai.module.social.comment.entity.Comment;
import com.jcx.jiachangcai.module.social.comment.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 评论表 前端控制器
 * </p>
 *
 * @author wsc
 * @since 2026-08-01
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private ICommentService service;
   //获取单个菜谱的评论
    @GetMapping("/getComment")
    public List<Comment> getComment(Long recipe_id) {
        return service.getCommentOfRecipe(recipe_id);
    }

    //发布评论
    @PostMapping("/addComment")
    public String addComment(Comment comment){
        service.addComment(comment);
        return "Commentok";
    }

    //获取菜谱评论数量
    @GetMapping("/count")
    public Long getCount(@RequestParam Long recipeId) {
        com.jcx.jiachangcai.module.recipe.entity.Recipe recipe = new com.jcx.jiachangcai.module.recipe.entity.Recipe();
        recipe.setRecipeId(recipeId);
        return service.getCountOfRecipe(recipe);
    }


}
