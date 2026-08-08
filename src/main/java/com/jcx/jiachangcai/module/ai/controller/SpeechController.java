package com.jcx.jiachangcai.module.ai.controller;

import com.jcx.jiachangcai.module.ai.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    @Autowired
    private SpeechService speechService;

    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> recognize(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "音频文件为空"));
        }

        try {
            byte[] audio = file.getBytes();
            String contentType = file.getContentType();
            String text = speechService.recognize(audio, contentType);

            if (text == null || text.isEmpty()) {
                return ResponseEntity.ok(Map.of("text", ""));
            }

            return ResponseEntity.ok(Map.of("text", text));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "音频处理失败: " + e.getMessage()));
        }
    }
}
