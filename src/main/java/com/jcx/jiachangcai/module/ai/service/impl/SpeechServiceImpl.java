package com.jcx.jiachangcai.module.ai.service.impl;

import com.jcx.jiachangcai.module.ai.service.SpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.UUID;

@Service
public class SpeechServiceImpl implements SpeechService {

    private static final Logger log = LoggerFactory.getLogger(SpeechServiceImpl.class);

    @Value("${speech.recognize.api-key:}")
    private String apiKey;

    @Value("${speech.recognize.base-url:https://api.siliconflow.cn}")
    private String baseUrl;

    @Value("${speech.recognize.model:FunAudioLLM/SenseVoiceSmall}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String recognize(byte[] audio, String contentType) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Speech recognition API key not configured");
            return "";
        }

        try {
            File tempFile = File.createTempFile("speech-", getExtension(contentType));
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(audio);
            }

            try {
                String result = callTranscriptionApi(tempFile);
                log.info("Speech recognized: {} chars", result != null ? result.length() : 0);
                return result != null ? result.trim() : "";
            } finally {
                tempFile.delete();
            }
        } catch (IOException e) {
            log.error("Speech recognition failed", e);
            return "";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Speech recognition interrupted", e);
            return "";
        }
    }

    private String callTranscriptionApi(File audioFile) throws IOException, InterruptedException {
        String boundary = "Boundary-" + UUID.randomUUID();

        // Build multipart body
        ByteArrayOutputStream body = new ByteArrayOutputStream();

        // model part
        writePart(body, boundary, "model", model);

        // file part
        body.write(("--" + boundary + "\r\n").getBytes());
        body.write(("Content-Disposition: form-data; name=\"file\"; filename=\"audio" + getExtensionFromFile(audioFile) + "\"\r\n").getBytes());
        body.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes());
        body.write(Files.readAllBytes(audioFile.toPath()));
        body.write("\r\n".getBytes());

        // Close boundary
        body.write(("--" + boundary + "--\r\n").getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/audio/transcriptions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Parse JSON response: {"text": "..."}
            String json = response.body();
            return extractTextFromJson(json);
        } else {
            log.error("STT API error {}: {}", response.statusCode(), response.body());
            return "";
        }
    }

    private void writePart(OutputStream out, String boundary, String name, String value) throws IOException {
        out.write(("--" + boundary + "\r\n").getBytes());
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
        out.write(value.getBytes());
        out.write("\r\n".getBytes());
    }

    private String extractTextFromJson(String json) {
        if (json == null || json.isEmpty()) return "";
        // Simple JSON extraction: look for "text":"..."
        int idx = json.indexOf("\"text\"");
        if (idx < 0) return "";
        int start = json.indexOf("\"", idx + 6);
        if (start < 0) return "";
        int end = json.indexOf("\"", start + 1);
        if (end < 0) return "";
        String text = json.substring(start + 1, end);
        // Unescape common JSON escapes
        return text.replace("\\n", "\n").replace("\\t", "\t").replace("\\\"", "\"");
    }

    private String getExtension(String contentType) {
        if (contentType == null) return ".webm";
        if (contentType.contains("webm")) return ".webm";
        if (contentType.contains("mp4") || contentType.contains("m4a")) return ".m4a";
        if (contentType.contains("ogg") || contentType.contains("opus")) return ".ogg";
        if (contentType.contains("wav")) return ".wav";
        return ".webm";
    }

    private String getExtensionFromFile(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot) : ".webm";
    }
}
