package com.jcx.jiachangcai.module.ai.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.jcx.jiachangcai.module.ingredient.mapper.IngredientMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 食材管理 ToolCalling 工具。每次请求创建新实例，userId 通过构造器注入。
 */
public class IngredientTools {

    private final Long userId;
    private final IngredientMapper mapper;

    private static final java.util.Set<String> VALID_CATEGORIES = java.util.Set.of("蔬菜", "生禽", "蛋类", "水产", "豆制品", "其他");

    private static final java.util.Map<String, String> CATEGORY_ALIASES;

    static {
        CATEGORY_ALIASES = new java.util.HashMap<>();
        CATEGORY_ALIASES.put("鸡蛋", "蛋类");
        CATEGORY_ALIASES.put("鸭蛋", "蛋类");
        CATEGORY_ALIASES.put("鹅蛋", "蛋类");
        CATEGORY_ALIASES.put("鹌鹑蛋", "蛋类");
        CATEGORY_ALIASES.put("猪肉", "生禽");
        CATEGORY_ALIASES.put("牛肉", "生禽");
        CATEGORY_ALIASES.put("羊肉", "生禽");
        CATEGORY_ALIASES.put("鸡肉", "生禽");
        CATEGORY_ALIASES.put("鸭肉", "生禽");
        CATEGORY_ALIASES.put("鱼", "水产");
        CATEGORY_ALIASES.put("虾", "水产");
        CATEGORY_ALIASES.put("蟹", "水产");
        CATEGORY_ALIASES.put("贝", "水产");
        CATEGORY_ALIASES.put("豆腐", "豆制品");
        CATEGORY_ALIASES.put("豆浆", "豆制品");
        CATEGORY_ALIASES.put("豆皮", "豆制品");
        CATEGORY_ALIASES.put("腐竹", "豆制品");
        CATEGORY_ALIASES.put("青菜", "蔬菜");
        CATEGORY_ALIASES.put("白菜", "蔬菜");
        CATEGORY_ALIASES.put("萝卜", "蔬菜");
        CATEGORY_ALIASES.put("番茄", "蔬菜");
        CATEGORY_ALIASES.put("黄瓜", "蔬菜");
    }

    public IngredientTools(Long userId, IngredientMapper mapper) {
        this.userId = userId;
        this.mapper = mapper;
    }

    private String normalizeCategory(String name, String category) {
        if (category != null && VALID_CATEGORIES.contains(category)) return category;
        if (name != null && CATEGORY_ALIASES.containsKey(name)) return CATEGORY_ALIASES.get(name);
        return "其他";
    }

    @Tool(description = "查询用户冰箱里当前有哪些食材。返回食材名称、储存方式、剩余保质天数。已过期的会标注'已过期'。")
    public String queryMyIngredients() {
        if (userId == null) {
            return "无法获取用户信息，请先登录。";
        }
        List<Ingredient> list = mapper.selectList(
                new LambdaQueryWrapper<Ingredient>().eq(Ingredient::getUserId, userId)
        );
        if (list.isEmpty()) {
            return "冰箱里暂时没有食材记录，请先添加食材。";
        }
        LocalDate today = LocalDate.now();
        return list.stream()
                .sorted(Comparator.comparing(Ingredient::getCreateTime).reversed())
                .map(ing -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("- ").append(ing.getName()).append("（").append(ing.getCategory());
                    if (ing.getStorageMethod() != null) {
                        sb.append("，").append(ing.getStorageMethod());
                    }
                    if (ing.getExpireDays() == null || ing.getExpireDate() == null) {
                        sb.append("）保质期未知");
                    } else {
                        long remaining = ChronoUnit.DAYS.between(today, ing.getExpireDate().toLocalDate());
                        if (remaining < 0) {
                            sb.append("）已过期").append(Math.abs(remaining)).append("天");
                        } else if (remaining == 0) {
                            sb.append("）今天到期");
                        } else if (remaining <= 1) {
                            sb.append("）临近过期，剩余").append(remaining).append("天");
                        } else {
                            sb.append("）剩余").append(remaining).append("天");
                        }
                    }
                    return sb.toString();
                })
                .collect(Collectors.joining("\n"));
    }

    @Tool(description = "向用户冰箱添加一种新食材。需要食材名称、分类、储存方式（冷藏/冷冻/常温）、购买日期（yyyy-MM-dd）。添加成功后，请根据保质期规则表确定保质天数，然后调用 updateExpiryStatus 写入。")
    public String addIngredient(
            @ToolParam(description = "食材名称，如：鸡蛋、西红柿、猪肉") String name,
            @ToolParam(description = "食材分类：蔬菜、生禽、蛋类、水产、豆制品、其他") String category,
            @ToolParam(description = "储存方式：冷藏、冷冻、常温。用户没说则默认冷藏") String storageMethod,
            @ToolParam(description = "购买日期，格式yyyy-MM-dd。用户没说则默认当天") String purchaseDateStr) {
        if (userId == null) {
            return "无法获取用户信息，请先登录。";
        }
        String normalized = normalizeCategory(name, category);
        if (storageMethod == null || storageMethod.isBlank()) storageMethod = "冷藏";

        LocalDate purchaseDate;
        try {
            purchaseDate = (purchaseDateStr != null && !purchaseDateStr.isBlank())
                    ? LocalDate.parse(purchaseDateStr) : LocalDate.now();
        } catch (Exception e) {
            purchaseDate = LocalDate.now();
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setUserId(userId);
        ingredient.setName(name);
        ingredient.setCategory(normalized);
        ingredient.setStorageMethod(storageMethod);
        ingredient.setPurchaseDate(purchaseDate);
        ingredient.setCreateTime(LocalDateTime.now());
        mapper.insert(ingredient);
        return "已添加：" + name + "（" + normalized + "，储存方式：" + storageMethod
                + "）。请根据保质期规则表确定保质天数，然后调用 updateExpiryStatus 写入数据库。";
    }

    @Tool(description = "从用户冰箱删除一种食材。需要用户明确指定要删除的食材名称。如果有多个同名食材，删除最早添加的那个。")
    public String deleteIngredient(
            @ToolParam(description = "要删除的食材名称，如：鸡蛋") String name) {
        if (userId == null) {
            return "无法获取用户信息，请先登录。";
        }
        List<Ingredient> list = mapper.selectList(
                new LambdaQueryWrapper<Ingredient>()
                        .eq(Ingredient::getUserId, userId)
                        .eq(Ingredient::getName, name)
                        .orderByAsc(Ingredient::getCreateTime)
        );
        if (list.isEmpty()) {
            return "未找到名为" + name + "的食材。";
        }
        mapper.deleteById(list.get(0).getIngredientId());
        return "已删除食材：" + name;
    }

    @Tool(description = "根据保质期规则表确定食材的保质天数后，调用此工具将保质期信息写入数据库。必须先调用 addIngredient 成功后再调用此工具。")
    public String updateExpiryStatus(
            @ToolParam(description = "食材名称，必须与刚才 addIngredient 时一致的名称") String name,
            @ToolParam(description = "保质天数，根据规则表查到的数字，如30") int expireDays) {
        if (userId == null) {
            return "无法获取用户信息，请先登录。";
        }
        List<Ingredient> list = mapper.selectList(
                new LambdaQueryWrapper<Ingredient>()
                        .eq(Ingredient::getUserId, userId)
                        .eq(Ingredient::getName, name)
                        .isNull(Ingredient::getExpireDays)
                        .orderByDesc(Ingredient::getCreateTime)
        );
        if (list.isEmpty()) {
            return "未找到需要更新保质期的食材：" + name;
        }
        Ingredient ing = list.get(0);
        ing.setExpireDays(expireDays);
        if (ing.getPurchaseDate() != null) {
            // 防御：拒绝过早或过晚的购买日期（AI 可能产生幻觉日期）
            LocalDate today = LocalDate.now();
            if (ing.getPurchaseDate().isBefore(today.minusYears(1))) {
                ing.setPurchaseDate(today);
            } else if (ing.getPurchaseDate().isAfter(today.plusDays(1))) {
                ing.setPurchaseDate(today);
            }
            ing.setExpireDate(ing.getPurchaseDate().plusDays(expireDays).atStartOfDay());
        }
        mapper.updateById(ing);

        String expireInfo = ing.getExpireDate() != null
                ? "，预计" + ing.getExpireDate().toLocalDate() + "前食用"
                : "";
        return "已更新：" + name + " 保质期 " + expireDays + " 天" + expireInfo;
    }

}
