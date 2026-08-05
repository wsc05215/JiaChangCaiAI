package com.jcx.jiachangcai.module.ai.service.impl;

import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.prompt.AiPrompts;
import com.jcx.jiachangcai.module.ai.service.AiChatService;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private VectorStore vectorStore;

    private final ConcurrentHashMap<String, Integer> trialCount = new ConcurrentHashMap<>();

    @Override
    public Flux<String> chat(Long userId, AiChatType type, String message) {
        // CHEF 和 CUSTOMER_SERVICE 免费，其余非会员限5次
        if (type != AiChatType.CHEF && type != AiChatType.CUSTOMER_SERVICE
                && !memberService.getisMember(userId)) {
            String key = userId + "_" + type;
            int count = trialCount.getOrDefault(key, 0);
            if (count >= 5) {
                return Flux.just("❌您是非会员，免费5次对话额度已用完，请开通会员继续使用！");
            }
            trialCount.put(key, count + 1);
        }

        // 客服模式：先检索知识库，再拼入 prompt
        String systemPrompt;
        if (type == AiChatType.CUSTOMER_SERVICE) {
            String context = retrieveContext(message);
            systemPrompt = AiPrompts.get(type)
                    .replace("{context}", context)
                    .replace("{question}", message);
        } else {
            systemPrompt = AiPrompts.get(type);
        }

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId + "_" + type))
                .stream()
                .content();
    }

    private String retrieveContext(String query) {
        try {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(3)
                            .similarityThreshold(0.5)
                            .build()
            );
            if (docs.isEmpty()) {
                return "暂无相关参考资料。";
            }
            return docs.stream()
                    .map(doc -> "---\n" + doc.getText())
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "参考资料检索失败，请按通用政策回答。";
        }
    }
}
