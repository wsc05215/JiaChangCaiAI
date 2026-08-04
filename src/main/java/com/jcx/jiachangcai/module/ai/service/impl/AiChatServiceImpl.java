package com.jcx.jiachangcai.module.ai.service.impl;

import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.prompt.AiPrompts;
import com.jcx.jiachangcai.module.ai.service.AiChatService;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private IMemberService memberService;

    private final ConcurrentHashMap<String, Integer> trialCount = new ConcurrentHashMap<>();

    @Override
    public Flux<String> chat(Long userId, AiChatType type, String message) {
        // CHEF 模式免费，其余模式非会员限5次
        if (type != AiChatType.CHEF && !memberService.getisMember(userId)) {
            String key = userId + "_" + type;
            int count = trialCount.getOrDefault(key, 0);
            if (count >= 5) {
                return Flux.just("❌您是非会员，免费5次对话额度已用完，请开通会员继续使用！");
            }
            trialCount.put(key, count + 1);
        }

        return chatClient.prompt()
                .system(AiPrompts.get(type))
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId + "_" + type))
                .stream()
                .content();
    }
}
