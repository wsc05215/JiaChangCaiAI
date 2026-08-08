package com.jcx.jiachangcai.module.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcx.jiachangcai.module.ai.entity.CustomRecord;
import com.jcx.jiachangcai.module.ai.enums.AiChatType;
import com.jcx.jiachangcai.module.ai.mapper.CustomRecordMapper;
import com.jcx.jiachangcai.module.ai.service.ICustomRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomRecordServiceImpl extends ServiceImpl<CustomRecordMapper, CustomRecord> implements ICustomRecordService {

    @Override
    public void saveRecord(Long userId, AiChatType type, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        CustomRecord record = new CustomRecord();
        record.setUserId(userId);
        record.setType(type.name());
        record.setTitle(extractTitle(content, type));
        record.setContent(content);
        record.setCreateTime(LocalDateTime.now());

        // 解析 markdown 提取结构化字段
        parseMarkdownToRecord(content, record);

        save(record);
    }

    @Override
    public void saveWeeklyPlan(Long userId, AiChatType type, String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        // 提取主标题（如 "7天定制食谱 - 减脂版"）
        String mainTitle = "";
        var allLines = content.lines().map(String::trim).filter(l -> !l.isEmpty()).toList();
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if (line.startsWith("# ") && !line.startsWith("## ")) {
                String heading = line.substring(2).trim();
                if (heading.equals("用户需求总结") || heading.equals("一周食材采购清单")
                        || heading.equals("一周营养建议")) {
                    continue;
                }
                // 找到主标题或取下一行
                if (heading.equals("7天定制食谱")) {
                    for (int j = i + 1; j < allLines.size(); j++) {
                        String next = allLines.get(j);
                        if (next.startsWith("#")) break;
                        if (!next.startsWith("-") && !next.startsWith("*")) {
                            mainTitle = next;
                            break;
                        }
                    }
                    if (mainTitle.isEmpty()) mainTitle = heading;
                } else {
                    mainTitle = heading;
                }
                break;
            }
        }
        if (mainTitle.isBlank()) {
            mainTitle = "定制食谱";
        }

        // 提取尾部（采购清单 + 营养建议）
        String tail = "";
        int tailStart = content.indexOf("## 一周食材采购清单");
        if (tailStart < 0) tailStart = content.indexOf("## 一周营养建议");
        if (tailStart >= 0) {
            tail = "\n\n" + content.substring(tailStart).trim();
        }

        // 按天拆分：## 第一天（周一）/ ## 第二天（周二）...
        Pattern dayPattern = Pattern.compile("(##\\s*第[一二三四五六七]天[^\\n]*)");
        Matcher matcher = dayPattern.matcher(content);

        int lastEnd = 0;
        String lastDayHeading = null;
        int dayIndex = 0;

        while (matcher.find()) {
            if (lastDayHeading != null) {
                // 截取上一个 day 的内容
                String dayContent = content.substring(lastEnd, matcher.start()).trim();
                saveOneDay(userId, type, mainTitle, lastDayHeading, dayContent, dayIndex);
            }
            lastDayHeading = matcher.group(1).trim();
            lastEnd = matcher.start();
            dayIndex++;
        }

        // 最后一天
        if (lastDayHeading != null) {
            String dayContent = content.substring(lastEnd);
            // 截掉尾部（采购清单等）
            int tIdx = dayContent.indexOf("## 一周食材采购清单");
            if (tIdx < 0) tIdx = dayContent.indexOf("## 一周营养建议");
            if (tIdx >= 0) {
                dayContent = dayContent.substring(0, tIdx).trim();
            }
            dayContent = dayContent.trim();
            saveOneDay(userId, type, mainTitle, lastDayHeading, dayContent, dayIndex);
        }
    }

    private void saveOneDay(Long userId, AiChatType type, String mainTitle,
                            String dayHeading, String dayContent, int dayIndex) {
        // 从 dayHeading 提取 "第一天（周一）"
        String dayLabel = dayHeading.replaceAll("^#{1,3}\\s*", "").trim();
        String title = mainTitle + " · " + dayLabel;
        if (title.length() > 50) {
            title = dayLabel + "食谱";
        }

        String fullContent = "# " + mainTitle + "\n\n" + dayHeading + "\n" + dayContent;

        CustomRecord record = new CustomRecord();
        record.setUserId(userId);
        record.setType(type.name());
        record.setTitle(title);
        record.setContent(fullContent);
        record.setCreateTime(LocalDateTime.now());

        // 对每天的内容做结构化解析
        parseMarkdownToRecord(fullContent, record);

        save(record);
    }

    @Override
    public List<CustomRecord> listByUserIdAndType(Long userId, AiChatType type) {
        LambdaQueryWrapper<CustomRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomRecord::getUserId, userId)
                .eq(CustomRecord::getType, type.name())
                .orderByDesc(CustomRecord::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<CustomRecord> listByUserId(Long userId) {
        LambdaQueryWrapper<CustomRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomRecord::getUserId, userId)
                .orderByDesc(CustomRecord::getCreateTime);
        return list(wrapper);
    }

    // ========== Markdown 解析 ==========

    private static final Pattern HEADING = Pattern.compile("^#{1,3}\\s");

    private void parseMarkdownToRecord(String content, CustomRecord record) {
        String[] lines = content.split("\\R");
        String currentSection = null;
        StringBuilder descBuf = new StringBuilder();
        List<String[]> ingList = new ArrayList<>();
        List<String> stepList = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // 章节标题行
            if (HEADING.matcher(trimmed).find()) {
                if (trimmed.contains("菜品简介") || trimmed.contains("简介")) {
                    currentSection = "desc";
                } else if (trimmed.contains("基本信息")) {
                    currentSection = "info";
                } else if (trimmed.contains("食材清单") || trimmed.contains("食材列表") || trimmed.contains("所需食材")) {
                    currentSection = "ingredients";
                } else if (trimmed.contains("烹饪步骤") || trimmed.contains("制作步骤") || trimmed.contains("操作步骤")) {
                    currentSection = "steps";
                } else {
                    currentSection = null; // 其他标题不处理
                }
                continue;
            }

            // 按当前章节解析
            if ("desc".equals(currentSection)) {
                if (descBuf.length() > 0) descBuf.append(" ");
                descBuf.append(trimmed);
            } else if ("info".equals(currentSection)) {
                Matcher m = Pattern.compile("^[-*•]\\s*(.+?)[：:]\\s*(.+)").matcher(trimmed);
                if (m.find()) {
                    String key = m.group(1).replaceAll("\\*+", "").trim();
                    String value = m.group(2).replaceAll("\\*+", "").trim();
                    if (key.contains("烹饪时长") || key.contains("时间")) {
                        record.setCookTime(value);
                    } else if (key.contains("难度")) {
                        record.setDifficulty(value);
                    }
                }
            } else if ("ingredients".equals(currentSection)) {
                Matcher m = Pattern.compile("^[-*•]\\s*(?:\\*\\*)?(.+?)(?:\\*\\*)?[：:]\\s*(.+)").matcher(trimmed);
                if (m.find()) {
                    ingList.add(new String[]{m.group(1).trim(), m.group(2).trim()});
                }
            } else if ("steps".equals(currentSection)) {
                Matcher m = Pattern.compile("^\\d+[.)、]\\s*(.+)").matcher(trimmed);
                if (m.find()) {
                    stepList.add(m.group(1).trim());
                }
            }
        }

        // 写入结构化字段
        String desc = descBuf.toString().trim();
        if (!desc.isEmpty()) {
            if (desc.length() > 500) desc = desc.substring(0, 500);
            record.setDescription(desc);
        }
        if (!ingList.isEmpty()) {
            record.setIngredients(toIngredientJson(ingList));
        }
        if (!stepList.isEmpty()) {
            record.setSteps(toStepsJson(stepList));
        }
    }

    private String toIngredientJson(List<String[]> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"name\":\"").append(escapeJson(list.get(i)[0]))
              .append("\",\"amount\":\"").append(escapeJson(list.get(i)[1]))
              .append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toStepsJson(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }

    // ========== 标题提取 ==========

    /**
     * 从 AI 回复的 markdown 内容中提取菜名作为标题。
     * 策略：扫描所有 # 一级标题找菜名 → 找含"食谱"的正文行 → 找最短正文行。
     */
    private String extractTitle(String content, AiChatType type) {
        if (content == null || content.isBlank()) {
            return type.getDisplayName() + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }

        var lines = content.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        String title = "";

        // 1. 扫描所有 # 一级标题，找第一个非章节标题（菜名）
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.startsWith("# ") || line.startsWith("## ")) continue;

            String heading = line.substring(2).trim();

            if (heading.equals("菜名") || heading.equals("7天定制食谱")) {
                // 模板占位标题，取下一行正文作为菜名
                for (int j = i + 1; j < lines.size(); j++) {
                    String next = lines.get(j);
                    if (next.startsWith("#")) break;
                    if (!next.startsWith("-") && !next.startsWith("*")) {
                        title = next;
                        break;
                    }
                }
                if (!title.isEmpty()) break;
            } else if (!isSectionHeading(heading)) {
                // 非章节标题就是菜名本身（如 "# 番茄炒蛋食谱"）
                title = heading;
                break;
            }
            // 是章节标题（菜品简介等），继续扫描后面的 # 标题
        }

        // 2. 没找到合适标题：优先找第一条含"食谱"的正文行（菜名通常带"食谱"）
        if (title.isEmpty()) {
            title = lines.stream()
                    .filter(line -> !line.startsWith("#") && !line.startsWith("-") && !line.startsWith("*"))
                    .filter(line -> line.contains("食谱"))
                    .findFirst()
                    .orElse("");
        }

        // 3. 还没找到：取最短的正文行（菜名通常较短，AI 废话通常很长）
        if (title.isEmpty()) {
            title = lines.stream()
                    .filter(line -> !line.startsWith("#") && !line.startsWith("-") && !line.startsWith("*"))
                    .min(java.util.Comparator.comparingInt(String::length))
                    .orElse("");
        }

        if (title.isEmpty()) {
            title = type.getDisplayName() + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
        // 7天方案：确保标题包含"7天"，前端依赖此关键词识别周计划
        if (!title.contains("7天") && isWeeklyContent(content)) {
            title = "7天" + title;
        }
        if (!title.endsWith("食谱") && !title.contains("食谱")) {
            String suffix = "食谱";
            if (title.length() + suffix.length() > 50) {
                title = title.substring(0, 50 - suffix.length()) + suffix;
            } else {
                title = title + suffix;
            }
        } else if (title.length() > 50) {
            title = title.substring(0, 50) + "...";
        }
        return title;
    }

    /** AI 输出模板中的章节标题，不是菜名本身 */
    private boolean isSectionHeading(String heading) {
        return heading.equals("菜品简介") || heading.equals("基本信息")
                || heading.equals("食材清单") || heading.equals("烹饪步骤")
                || heading.equals("小贴士")
                || heading.equals("用户需求总结") || heading.equals("一周食材采购清单")
                || heading.equals("一周营养建议");
    }

    /** 检测内容是否为7天方案格式 */
    private boolean isWeeklyContent(String content) {
        return content != null && content.contains("第一天")
                && (content.contains("早餐") || content.contains("午餐") || content.contains("采购清单"));
    }
}
