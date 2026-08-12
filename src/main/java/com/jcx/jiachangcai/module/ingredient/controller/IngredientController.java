package com.jcx.jiachangcai.module.ingredient.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.jcx.jiachangcai.module.ingredient.service.IIngredientService;
import com.jcx.jiachangcai.module.member.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {
    @Autowired
    private IIngredientService service;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    /** 阿里云百炼 Qwen-VL 视觉识别配置（见 application.properties dashscope.vision.*） */
    @Value("${dashscope.vision.api-key:}")
    private String visionApiKey;
    @Value("${dashscope.vision.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String visionBaseUrl;
    @Value("${dashscope.vision.model:qwen-vl-max}")
    private String visionModel;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** 识别图片时图片落盘目录（本机磁盘路径，视觉识别读取该文件） */
    private static final String IMG_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "ingredients").toString();

    private static final Set<String> VALID_CATEGORIES = Set.of("蔬菜", "生禽", "蛋类", "水产", "豆制品", "其他");

    /** 发给 Qwen-VL 的识别提示词：强制结构化 JSON，不含任何多余说明 */
    private static final String VISION_PROMPT = """
            你是食材记账助手。请识别图片里的购物内容，并判断每件食材的保质期：
            若是购物小票/发票/收据，提取每件商品；若是食材实物照片，识别每个食材。
            严格只输出 JSON，不要任何解释，不要 markdown 代码块。格式如下：
            {"items":[{"name":"西红柿","category":"蔬菜","quantity":2,"unit":"个","expireDays":7}]}
            约束：
            - category 只能是 蔬菜、生禽、蛋类、水产、豆制品、其他 之一；
            - expireDays 为保质天数，默认按"冷藏储存"估计，典型值参考：蔬菜7、蛋类30、生禽3、水产2、豆制品5、其他7；
              牛奶/酸奶等按包装标注估计（冷藏一般7-21天）；无法判断时给该分类的典型值，必须在1-365之间；
            - 没有把握的数量填 1，unit 可省略。
            """;

    @PostMapping("/addIngredient")
    public void addIngredient(Long userId, String name, String category, LocalDateTime createTime) {
        service.addIngredient(userId, name, category, createTime);
    }

    /**
     * 拍照识别食材（【不】入库，返回识别项供前端确认/编辑后再调 /saveBatch 保存）：
     * 上传图片 -> 落盘 -> 调阿里云百炼 Qwen-VL 识别 -> 解析JSON -> 规范化分类
     *
     * @param userId 用户ID
     * @param file   图片文件（小票或食材实物照均可，仅用于识别，用后即删，不入库）
     * @return {items: 识别出的食材列表(含 quantity/unit/expireDays)}
     */
    @PostMapping("/recognize")
    public Map<String, Object> recognize(@RequestParam Long userId,
                                         @RequestParam("file") MultipartFile file) throws Exception {
        if (!memberService.getisMember(userId)) {
            throw new RuntimeException("请先开通会员后再使用拍照记账");
        }
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要识别的图片");
        }

        // 1. 图片落盘，拿到服务器本机绝对路径（Qwen-VL 按该文件读取并转 base64）。图片仅用于识别，用后即删，不保存进记录
        File img = saveImage(file);
        String absPath = img.getAbsolutePath();
        try {
            // 2. 调用阿里云百炼 Qwen-VL 识别
            String raw = callVisionTool(absPath);

            // 3. 解析识别结果为结构化食材列表（此时不入库，待用户确认后 saveBatch）
            List<Ingredient> items = parseItems(raw);

            Map<String, Object> result = new HashMap<>();
            result.put("items", items);
            return result;
        } finally {
            // 识别结束无论成败都清理临时图片
            if (img.exists() && !img.delete()) {
                img.deleteOnExit();
            }
        }
    }

    /**
     * 用户确认/编辑后的批量入库（会员门禁 + 补全购买日期/储存方式等默认字段）。
     *
     * @param body {"userId": xxx, "items":[{name, category, quantity, unit, expireDays, purchaseDate?}]}
     * @return 实际入库的食材列表
     */
    @PostMapping("/saveBatch")
    public Map<String, Object> saveBatch(@RequestBody Map<String, Object> body) {
        Object uidObj = body.get("userId");
        Long userId = uidObj instanceof Number n ? n.longValue() : null;
        if (userId == null) {
            throw new RuntimeException("缺少用户信息");
        }
        if (!memberService.getisMember(userId)) {
            throw new RuntimeException("请先开通会员后再使用拍照记账");
        }
        JsonNode itemsNode = objectMapper.valueToTree(body.get("items"));
        List<Ingredient> items = new ArrayList<>();
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode node : itemsNode) {
                String name = node.path("name").asText(null);
                if (name == null || name.isBlank()) continue;
                Ingredient ing = new Ingredient();
                ing.setName(name.trim());
                ing.setCategory(normalizeCategory(node.path("category").asText(null)));
                ing.setQuantity(node.hasNonNull("quantity") && node.path("quantity").asInt(1) > 0
                        ? node.path("quantity").asInt(1) : 1);
                String unit = node.path("unit").asText(null);
                if (unit != null && !unit.isBlank()) ing.setUnit(unit.trim());
                // 保质期（用户可在确认弹窗里调整，1-365 天内有效；缺失交给入库规则表兜底）
                if (node.hasNonNull("expireDays")) {
                    int d = node.path("expireDays").asInt(0);
                    if (d >= 1 && d <= 365) ing.setExpireDays(d);
                }
                String purchaseDate = node.path("purchaseDate").asText(null);
                if (purchaseDate != null && !purchaseDate.isBlank()) {
                    try {
                        ing.setPurchaseDate(LocalDate.parse(purchaseDate));
                    } catch (Exception ignore) {
                        // 非法日期忽略，走服务端默认今天
                    }
                }
                items.add(ing);
            }
        }

        List<Ingredient> saved = service.addRecognizedItems(userId, items);
        Map<String, Object> result = new HashMap<>();
        result.put("savedCount", saved.size());
        result.put("items", saved);
        return result;
    }

    /** 调阿里云百炼 Qwen-VL（OpenAI-compatible 接口）识别图片，强制模型输出结构化 JSON */
    private String callVisionTool(String imagePath) {
        if (visionApiKey == null || visionApiKey.isBlank()) {
            throw new RuntimeException("未配置阿里云百炼视觉识别 API Key（dashscope.vision.api-key）");
        }
        try {
            // 1. 图片转 base64 data URL（qwen-vl 走多模态消息格式）
            String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(imagePath)));
            String dataUrl = "data:" + mimeOf(imagePath) + ";base64," + base64;

            // 2. 组装多模态消息：图片 + 提示词
            List<Map<String, Object>> content = new ArrayList<>();
            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("type", "image_url");
            imagePart.put("image_url", Map.of("url", dataUrl));
            content.add(imagePart);
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("type", "text");
            textPart.put("text", VISION_PROMPT);
            content.add(textPart);

            Map<String, Object> body = new HashMap<>();
            body.put("model", visionModel);
            body.put("messages", List.of(Map.of("role", "user", "content", content)));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(visionBaseUrl + "/chat/completions"))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + visionApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            // 3. 解析 OpenAI-compatible 响应：choices[0].message.content 即为结构化 JSON 文本
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("视觉识别接口调用失败，HTTP " + resp.statusCode() + "：" + truncate(resp.body(), 300));
            }
            JsonNode choices = objectMapper.readTree(resp.body()).path("choices");
            String raw = choices.isArray() && choices.size() > 0
                    ? choices.get(0).path("message").path("content").asText(null)
                    : null;
            if (raw == null || raw.isBlank()) {
                throw new RuntimeException("视觉识别接口未返回内容，原始返回：" + truncate(resp.body(), 300));
            }
            return raw;
        } catch (Exception e) {
            throw new RuntimeException("调用视觉识别工具失败: " + e.getMessage(), e);
        }
    }

    /** 按图片文件扩展名推断 MIME（用于 data URL 前缀） */
    private String mimeOf(String imagePath) {
        String p = imagePath.toLowerCase();
        if (p.endsWith(".png")) return "image/png";
        if (p.endsWith(".webp")) return "image/webp";
        if (p.endsWith(".heic")) return "image/heic";
        if (p.endsWith(".heif")) return "image/heif";
        return "image/jpeg";
    }

    /** 解析模型返回的 JSON（容忍 markdown 代码块/前后缀文字），返回已规范化分类的食材列表 */
    private List<Ingredient> parseItems(String raw) {
        try {
            JsonNode root = objectMapper.readTree(extractJson(raw));
            JsonNode items = root.path("items");
            List<Ingredient> list = new ArrayList<>();
            if (items.isArray()) {
                for (JsonNode node : items) {
                    String name = node.path("name").asText(null);
                    if (name == null || name.isBlank()) continue;
                    Ingredient ing = new Ingredient();
                    ing.setName(name.trim());
                    ing.setCategory(normalizeCategory(node.path("category").asText(null)));
                    // 数量/单位：模型返回即保留，缺失时数量默认 1
                    ing.setQuantity(node.hasNonNull("quantity") && node.path("quantity").asInt(1) > 0
                            ? node.path("quantity").asInt(1) : 1);
                    String unit = node.path("unit").asText(null);
                    if (unit != null && !unit.isBlank()) ing.setUnit(unit.trim());
                    // 保质期：模型判断，1-365 天内有效；没给则交给入库时的规则表兜底
                    if (node.hasNonNull("expireDays")) {
                        int d = node.path("expireDays").asInt(0);
                        if (d >= 1 && d <= 365) ing.setExpireDays(d);
                    }
                    list.add(ing);
                }
            }
            if (list.isEmpty()) {
                throw new RuntimeException("未能从图片中识别出食材，原始返回：" + truncate(raw, 300));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("识别结果解析失败：" + e.getMessage() + "，原始返回：" + truncate(raw, 300), e);
        }
    }

    /** 取出字符串中最外层大括号包裹的 JSON 片段（容忍模型在 JSON 前后夹带文字/代码块/列表包装） */
    private String extractJson(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        // 1. 若模型把 JSON 包在 [{"text":"..."}] 里，先取出 text 字段内容
        if (t.startsWith("[")) {
            try {
                JsonNode arr = objectMapper.readTree(t);
                if (arr.isArray() && arr.size() > 0) {
                    String text = arr.get(0).path("text").asText(null);
                    if (text != null) t = text;
                }
            } catch (Exception ignore) {
                // 不是数组包装，走下面的兜底逻辑
            }
        }
        // 2. 取最外层大括号片段（模型可能在前后夹带文字/代码块）
        int start = t.indexOf('{');
        int end = t.lastIndexOf('}');
        if (start >= 0 && end > start) return t.substring(start, end + 1);
        return t;
    }

    /** 分类规范化：必须是六个合法分类之一，否则归入"其他" */
    private String normalizeCategory(String category) {
        if (category != null && VALID_CATEGORIES.contains(category)) return category;
        return "其他";
    }

    /** 图片类型/扩展名白名单 */
    private static final Set<String> ALLOWED_EXTS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".heic", ".heif");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/heic", "image/heif");
    /** 单张识别图片大小上限 10MB */
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;

    /** 图片落盘，返回落盘文件（本机绝对路径 + 对外相对地址由调用方计算） */
    private File saveImage(MultipartFile file) throws IOException {
        validateImage(file);
        File dir = new File(IMG_DIR);
        if (!dir.exists()) dir.mkdirs();
        String ext = getExt(file.getOriginalFilename());
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        File target = new File(dir, name);
        file.transferTo(target);
        return target;
    }

    /** 校验图片类型/扩展名/大小，非法直接抛异常 */
    private void validateImage(MultipartFile file) {
        String ext = getExt(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTS.contains(ext)) {
            throw new RuntimeException("仅支持 jpg/jpeg/png/webp/heic/heif 格式的图片");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()
                && !contentType.equalsIgnoreCase("application/octet-stream")
                && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("仅支持 jpg/jpeg/png/webp/heic/heif 格式的图片");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("图片大小不能超过 10MB");
        }
    }

    private String getExt(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    private String truncate(String s, int max) {
        if (s == null) return "null";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    @GetMapping("/list")
    public List<Ingredient> listIngredients(Long userId) {
        return service.listByUserId(userId);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestParam Long userId) {
        long total = service.countByUserId(userId);
        long nearExpiry = service.countNearExpiry(userId);
        long expired = service.countExpired(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("nearExpiry", nearExpiry);
        result.put("expired", expired);
        return result;
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable Long id) {
        service.deleteIngredient(id);
        return "ok";
    }
}
