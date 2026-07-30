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
@RequestMapping("/MyRecipeChatOfAI")
public class MyRecipeChatOfAI {

    private final ChatClient chatClient;

    @Autowired
    private IMemberService service;

    private final ConcurrentHashMap<Long, Integer> userChatCount = new ConcurrentHashMap<>();

    private static final String SYSTEM_PROMPT = """
            你是一周食谱专属生成助手，你的职责是根据用户需求生成一周食谱。
            规则：
            1. 用户只要表达了想要一周食谱的意图（不管措辞如何，比如"给我一周食谱""帮我安排下周吃什么""糖尿病一周食谱"等），就直接生成。
            2. 输出格式：按周一至周日划分，每日搭配餐食，清晰列出菜品名称。
            3. 只有与饮食完全无关的话题（闲聊、天气、新闻等），才回复：❌抱歉！我仅支持生成一周食谱，请向我提出一周膳食搭配需求。
            4. 禁止闲聊，严格遵守功能边界。
            """;

    public MyRecipeChatOfAI(ChatClient.Builder builder) {
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
