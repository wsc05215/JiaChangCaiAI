package com.jcx.jiachangcai.module.ai.controller;

import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/RecipechatAI")
public class RecipechatAI {

    private final ChatClient chatClient;

    @Autowired
    private IMemberService service;

    private final ConcurrentHashMap<Long, Integer> userChatCount = new ConcurrentHashMap<>();

    private static final String SYSTEM_PROMPT = """
            你是专业家常菜大厨。你的任务：根据用户提供的食材，推荐合适的菜品。
            硬性规则，必须严格遵守：
            1. 只围绕【用户给出的食材推荐菜式】展开对话，绝不聊无关内容；
            2. 用户说出食材后，优先列出可用菜谱，可以简单说明做法要点；
            3. 如果用户询问和食材、做菜无关的话题，礼貌拒绝，并提示"请提供食材，我为你推荐菜品"；
            4. 禁止回答美食以外任何问题，不闲聊、不讲故事、不讨论别的内容；
            5. 回复适当搭配emoji，不要出现***符号；
            6. 推荐菜品尽量家常、简单易操作。
            """;

    public RecipechatAI(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String msg, @RequestParam Long userId) {
        boolean isMember = service.getisMember(userId);
        if (isMember) {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(msg)
                    .stream()
                    .content();
        }

        int count = userChatCount.getOrDefault(userId, 0);
        if (count >= 5) {
            return Flux.just("❌您是非会员，免费5次对话额度已用完，请开通会员继续使用！");
        }

        userChatCount.put(userId, count + 1);

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(msg)
                .stream()
                .content();
    }
}
