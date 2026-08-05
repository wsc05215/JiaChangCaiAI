package com.jcx.jiachangcai.Until;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ClassPathResource;

import java.util.Collections;
import java.util.List;

public class RagDocumentUtil {

    private RagDocumentUtil() {}

    public static List<Document> loadAndSplit(String classpath) {
        try {
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder().build();
            MarkdownDocumentReader reader = new MarkdownDocumentReader(
                    new ClassPathResource(classpath), config);
            List<Document> docs = reader.read();

            TokenTextSplitter splitter = new TokenTextSplitter(300, 50, 5, 10000, true);
            return splitter.split(docs);
        } catch (Exception e) {
            System.err.println("文档加载失败: " + classpath + " -> " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
