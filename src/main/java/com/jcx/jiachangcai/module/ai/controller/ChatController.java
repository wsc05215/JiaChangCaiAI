package com.jcx.jiachangcai.module.ai.controller;

import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * AI大厨对话 —— 教用户做某道菜
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private AiChatService aiChatService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String msg, @RequestParam Long userId) {
        return aiChatService.chat(userId, AiChatType.CHEF, msg);
    }
}
