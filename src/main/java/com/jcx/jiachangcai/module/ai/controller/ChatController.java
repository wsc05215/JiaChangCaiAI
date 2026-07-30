package com.jcx.jiachangcai.module.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一个大厨，只能回答关于菜谱做菜的知识,回复我不要出现***，可以适当用emoji";

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String msg, @RequestParam Long userId) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(msg)
                .stream()
                .content();
    }
}
