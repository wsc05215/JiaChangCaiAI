package com.jcx.jiachangcai.module.ai.service;

import com.jcx.jiachangcai.Until.RagDocumentUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnowledgeBaseInitService {

    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void init() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:kb/*.md");
            if (resources.length == 0) {
                System.out.println("kb目录下没有找到.md文档，跳过知识库初始化");
                return;
            }

            List<Document> allChunks = new ArrayList<>();
            for (Resource resource : resources) {
                String path = "kb/" + resource.getFilename();
                List<Document> chunks = RagDocumentUtil.loadAndSplit(path);
                if (!chunks.isEmpty()) {
                    allChunks.addAll(chunks);
                    System.out.println("已加载 [" + resource.getFilename() + "] → " + chunks.size() + " 个文档块");
                }
            }

            if (!allChunks.isEmpty()) {
                vectorStore.add(allChunks);
                System.out.println("知识库初始化完成，共 " + resources.length + " 个文件，写入 " + allChunks.size() + " 个文档块");
            } else {
                System.out.println("知识库没有可加载的文档内容");
            }
        } catch (IOException e) {
            System.err.println("知识库初始化失败（IO错误）: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
