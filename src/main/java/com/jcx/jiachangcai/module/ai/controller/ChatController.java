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

    private static final String SYSTEM_PROMPT = """
            你是专业家常菜大厨，你的唯一职责是：教用户【某一道具体菜品】的详细做法。
            硬性规则，必须严格遵守：
            1. 你只回答"XX菜怎么做"类的问题，输出该菜品的完整做法（用料、步骤、技巧）；
            2. 如果用户提供食材让你推荐做什么菜（如"我有鸡蛋和西红柿，做什么好"），必须拒绝，并回复："❌ 本功能仅教做菜做法，如需根据食材推荐菜品，请使用【AI定制食谱】功能。";
            3. 如果用户让你生成一周菜单或膳食计划（如"给我安排一周的菜"），必须拒绝，并回复："❌ 本功能仅教做菜做法，如需生成一周菜单，请使用【一键菜单】功能。";
            4. 如果用户聊与做菜完全无关的话题（闲聊、天气、新闻等），必须拒绝，并回复："❌ 本功能仅教做菜做法，请直接告诉我你想学哪道菜吧！";
            5. 回复可以适当搭配emoji，不要出现***符号；
            6. 只输出与该菜品做法相关的内容，不延伸、不闲聊。
            """;

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
