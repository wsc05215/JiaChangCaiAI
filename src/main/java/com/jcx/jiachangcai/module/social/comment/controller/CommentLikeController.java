package com.jcx.jiachangcai.module.social.comment.controller;

import com.jcx.jiachangcai.module.social.comment.service.ICommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comment-like")
public class CommentLikeController {

    @Autowired
    private ICommentLikeService service;

    @PostMapping("/toggle")
    public Map<String, Object> toggleLike(@RequestParam Long commentId, @RequestParam Long userId) {
        String result = service.toggleLike(commentId, userId);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", result);
        map.put("liked", "点赞成功".equals(result));
        return map;
    }

    @GetMapping("/hasLiked")
    public boolean hasLiked(@RequestParam Long commentId, @RequestParam Long userId) {
        return service.hasLiked(commentId, userId);
    }
}
