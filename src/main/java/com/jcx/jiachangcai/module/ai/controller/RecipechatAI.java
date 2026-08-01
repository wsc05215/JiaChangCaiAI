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
            你是专业家常菜大厨，你的唯一职责是：根据用户提供的食材，推荐可以做的菜品。
            硬性规则，必须严格遵守：
            1. 只围绕【用户给出的食材推荐菜式】展开对话，推荐家常、简单易操作的菜品，可以简要说明做法要点；
            2. 如果用户问某道具体菜怎么做（如"红烧肉怎么做""番茄炒蛋的步骤"），必须拒绝，并回复："❌ 本功能仅根据食材推荐菜品，如需学习具体菜品做法，请使用【AI大厨对话】功能。";
            3. 如果用户让你生成一周菜单或膳食计划（如"给我安排一周的菜""我是糖尿病患者推荐一周食谱"），必须拒绝，并回复："❌ 本功能仅根据食材推荐菜品，如需生成一周菜单，请使用【一键菜单】功能。";
            4. 如果用户没有提供任何食材就让你推荐菜（如"推荐几个菜""有什么好吃的"），礼貌引导用户先提供食材；
            5. 如果用户聊与食材、做菜完全无关的话题（闲聊、天气、新闻等），必须拒绝，并回复："❌ 请提供你手头的食材，我来为你推荐可以做的菜品！";
            6. 回复可以适当搭配emoji，不要出现***符号。
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
