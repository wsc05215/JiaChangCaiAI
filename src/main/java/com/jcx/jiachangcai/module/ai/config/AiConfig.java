package com.jcx.jiachangcai.module.ai.config;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class AiConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(List<McpSyncClient> mcpSyncClients){
        return new SyncMcpToolCallbackProvider(mcpSyncClients);
    }

    @Bean
    public ChatMemory chatMemory() {
        InMemoryChatMemoryRepository repository = new InMemoryChatMemoryRepository();
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    @Value("${spring.ai.openai.embedding.base-url}")
    private String embeddingBaseUrl;
    @Value("${spring.ai.openai.embedding.api-key}")
    private String embeddingApikey;
    @Value("${spring.ai.openai.embedding.options.model}")
    private String embeddingModelName;
    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiApi embeddingApi = OpenAiApi.builder()
                .baseUrl(embeddingBaseUrl)
                .apiKey(embeddingApikey)
                .build();
        return new OpenAiEmbeddingModel(embeddingApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model(embeddingModelName)
                        .build());
    }

    @Bean
    public PgVectorStore pgVectorStore(
            @Qualifier("pgDataSource")DataSource pgDataSource,
            EmbeddingModel embeddingModel){
        JdbcTemplate jdbcTemplate =new JdbcTemplate(pgDataSource);
        return PgVectorStore.builder(jdbcTemplate,embeddingModel)
                .vectorTableName("vector_store")
                .schemaName("vector")
                .dimensions(1024)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .initializeSchema(false)
                .build();
    }
}
