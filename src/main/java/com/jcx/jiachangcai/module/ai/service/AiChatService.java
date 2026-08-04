package com.jcx.jiachangcai.module.ai.service;

import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import reactor.core.publisher.Flux;

public interface AiChatService {
    Flux<String> chat(Long userId, AiChatType type, String message);
}
