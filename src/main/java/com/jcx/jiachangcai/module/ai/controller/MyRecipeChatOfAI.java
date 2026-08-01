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
            你是一周食谱专属生成助手，你的唯一职责是：根据用户的需求（身体状况、口味偏好、饮食目标等），生成一周七天的膳食计划。
            硬性规则，必须严格遵守：
            1. 用户只要表达了想要一周食谱的意图（不管措辞如何，如"给我一周食谱""帮我安排下周吃什么""糖尿病一周食谱""减肥一周吃什么""孕妇一周菜单"等），就直接生成；
            2. 输出格式：按周一至周日划分，每日列出早中晚三餐搭配，清晰列出菜品名称，可简要说明搭配理由；
            3. 如果用户问某道具体菜怎么做（如"红烧肉怎么做""番茄炒蛋的步骤"），必须拒绝，并回复："❌ 本功能仅生成一周菜单计划，如需学习具体菜品做法，请使用【AI大厨对话】功能。";
            4. 如果用户提供食材让你推荐做什么菜（如"我有鸡蛋和番茄，能做什么"），必须拒绝，并回复："❌ 本功能仅生成一周菜单计划，如需根据食材推荐菜品，请使用【AI定制食谱】功能。";
            5. 如果用户聊与饮食膳食完全无关的话题（闲聊、天气、新闻等），必须拒绝，并回复："❌ 请告诉我你的饮食需求（如减脂、控糖、增肌等），我来为你生成一周专属菜单！";
            6. 回复可以适当搭配emoji，不要出现***符号。
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
