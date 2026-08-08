package com.jcx.jiachangcai.module.upload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private static final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "recipes").toString();
    private static final String VIDEO_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "recipes", "video").toString();

    @GetMapping("/ping")
    public String ping() {
        return "upload controller ok";
    }

    @PostMapping("/images")
    public List<String> uploadImages(HttpServletRequest request) {
        List<String> urls = new ArrayList<>();
        try {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> files = multiRequest.getFiles("files");
            log.info("收到上传请求, 文件数={}", files.size());

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                log.info("目录已创建: {}", dir.getAbsolutePath());
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String ext = getExt(file.getOriginalFilename());
                String name = UUID.randomUUID().toString().replace("-", "") + ext;
                file.transferTo(new File(dir, name));
                urls.add("/uploads/recipes/" + name);
            }
        } catch (Exception e) {
            log.error("上传异常", e);
            throw new RuntimeException("上传失败: " + e.getMessage(), e);
        }
        return urls;
    }

    @PostMapping("/video")
    public String uploadVideo(HttpServletRequest request) {
        MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
        MultipartFile file = multi.getFile("file");

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择视频文件");
        }

        // 校验类型白名单
        String contentType = file.getContentType();
        List<String> allowedTypes = List.of("video/mp4", "video/webm", "video/quicktime");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new RuntimeException("不支持的视频格式，仅支持 mp4 / webm / mov");
        }

        // 校验大小
        if (file.getSize() > 100L * 1024 * 1024) {
            throw new RuntimeException("视频不能超过 100MB");
        }

        try {
            String name = UUID.randomUUID().toString().replace("-", "") + ".mp4";
            File dir = new File(VIDEO_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.transferTo(new File(dir, name));
            log.info("视频上传成功: {}", name);
            return "/uploads/recipes/video/" + name;
        } catch (IOException e) {
            log.error("视频上传失败", e);
            throw new RuntimeException("视频上传失败: " + e.getMessage(), e);
        }
    }

    private String getExt(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
