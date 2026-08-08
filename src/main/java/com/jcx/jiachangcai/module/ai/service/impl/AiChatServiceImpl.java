package com.jcx.jiachangcai.module.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.ai.entity.TrialUsage;
import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.mapper.TrialUsageMapper;
import com.jcx.jiachangcai.module.ai.prompt.AiPrompts;
import com.jcx.jiachangcai.module.ai.service.AiChatService;
import com.jcx.jiachangcai.module.ai.service.ICustomRecordService;
import com.jcx.jiachangcai.module.ai.tool.IngredientTools;
import com.jcx.jiachangcai.module.ingredient.mapper.IngredientMapper;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ICustomRecordService customRecordService;

    @Autowired
    private IngredientMapper ingredientMapper;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private TrialUsageMapper trialUsageMapper;

    @Override
    public Flux<String> chat(Long userId, AiChatType type, String message) {
        // CHEF 和 CUSTOMER_SERVICE 免费，其余非会员仅限试用1次
        if (type != AiChatType.CHEF && type != AiChatType.CUSTOMER_SERVICE
                && !memberService.getisMember(userId)) {
            LambdaQueryWrapper<TrialUsage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TrialUsage::getUserId, userId)
                   .eq(TrialUsage::getAiType, type.name());
            if (trialUsageMapper.selectCount(wrapper) > 0) {
                return Flux.just("您已试用过" + type.getDisplayName() + "功能，开通会员即可无限使用！");
            }
            // 首次试用，记录到数据库
            TrialUsage usage = new TrialUsage();
            usage.setUserId(userId);
            usage.setAiType(type.name());
            usage.setCreateTime(LocalDateTime.now());
            trialUsageMapper.insert(usage);
        }

        // 注入当前日期，防止 AI 使用训练截止日期
        String dateInfo = "今天是" + LocalDate.now() + "，" +
                java.time.DayOfWeek.from(LocalDate.now()).getDisplayName(
                        java.time.format.TextStyle.FULL, java.util.Locale.CHINESE) + "。\n\n";

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
        systemPrompt = dateInfo + systemPrompt;

        // 收集完整回复用于自动存储
        StringBuilder fullContent = new StringBuilder();

        var prompt = chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId + "_" + type));

        // 食材相关模式：注入 per-request 工具实例（userId 通过构造器注入，无需 ThreadLocal）
        if (type == AiChatType.AiFridgeFoodService
                || type == AiChatType.Oneclickmenu
                || type == AiChatType.CustomizedRecipe) {
            prompt = prompt.tools(new IngredientTools(userId, ingredientMapper));
        }

        return prompt.stream()
                .content()
                .doOnNext(fullContent::append)
                .doFinally(signalType -> {
                    // 定制食谱和一键菜谱：仅保存包含完整菜谱结构的回复，过滤中间对话
                    if (type == AiChatType.CustomizedRecipe || type == AiChatType.Oneclickmenu) {
                        String content = fullContent.toString();
                        if (!content.isBlank() && isCompleteRecipe(content)) {
                            try {
                                customRecordService.saveRecord(userId, type, content);
                            } catch (Exception ignored) {
                                // 存储失败不影响对话
                            }
                        }
                    }
                })
                .onErrorResume(e -> {
                    // 客户端断开连接（如关闭页面），静默处理，不打印堆栈
                    if (e instanceof java.io.IOException) {
                        return Flux.empty();
                    }
                    return Flux.error(e);
                });
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

    /**
     * 判断 AI 回复是否包含完整菜谱结构，过滤掉多轮对话中的中间确认、追问等非菜谱内容。
     * 支持单道菜谱（食材清单+烹饪步骤）和7天定制方案（第一天+采购清单）两种格式。
     */
    private boolean isCompleteRecipe(String content) {
        if (content == null || content.isBlank()) {
            return false;
        }
        // 7天定制食谱：包含"第一天"且包含"早餐/午餐/晚餐"或"采购清单"
        boolean isWeeklyPlan = content.contains("第一天")
                && (content.contains("早餐") || content.contains("午餐") || content.contains("采购清单"));
        if (isWeeklyPlan) {
            return true;
        }
        // 单道菜谱：包含食材清单和烹饪步骤
        boolean hasIngredients = content.contains("食材清单") || content.contains("食材列表") || content.contains("所需食材");
        boolean hasSteps = content.contains("烹饪步骤") || content.contains("制作步骤") || content.contains("操作步骤");
        return hasIngredients && hasSteps;
    }
}
