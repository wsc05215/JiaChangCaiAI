package com.jcx.jiachangcai.module.ai.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jcx.jiachangcai.module.ingredient.entity.Ingredient;
import com.jcx.jiachangcai.module.ingredient.mapper.IngredientMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDateTime;
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

    @Tool(description = "查询用户冰箱里当前有哪些食材。返回食材名称、分类、剩余保质天数。已过期的会标注'已过期'。调用此工具不需要任何参数。")
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
        return list.stream()
                .sorted(Comparator.comparing(Ingredient::getCreateTime).reversed())
                .map(ing -> {
                    long days = ing.getDaysUntilExpiry();
                    String status;
                    if (days == Long.MAX_VALUE) {
                        status = "保质期未知";
                    } else if (ing.isExpired()) {
                        status = "已过期" + Math.abs(days) + "天";
                    } else if (ing.isNearExpiry()) {
                        status = "临近过期，剩余" + days + "天";
                    } else {
                        status = "剩余" + days + "天";
                    }
                    return "- " + ing.getName() + "（" + ing.getCategory() + "）" + status;
                })
                .collect(Collectors.joining("\n"));
    }

    @Tool(description = "向用户冰箱添加一种新食材。需要用户明确提供食材名称和分类。分类必须是：蔬菜、生禽、蛋类、水产、豆制品、其他。添加成功后会自动记录当前时间。")
    public String addIngredient(
            @ToolParam(description = "食材名称，如：鸡蛋、西红柿、猪肉") String name,
            @ToolParam(description = "食材分类，必须是：蔬菜、生禽、蛋类、水产、豆制品、其他") String category) {
        if (userId == null) {
            return "无法获取用户信息，请先登录。";
        }
        String normalized = normalizeCategory(name, category);
        Ingredient ingredient = new Ingredient();
        ingredient.setUserId(userId);
        ingredient.setName(name);
        ingredient.setCategory(normalized);
        ingredient.setCreateTime(LocalDateTime.now());
        mapper.insert(ingredient);
        return "已成功添加食材：" + name + "（" + normalized + "）";
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
}
