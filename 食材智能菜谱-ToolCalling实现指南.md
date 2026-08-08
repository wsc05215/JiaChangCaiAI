# 食材保质期 — 硬编码改为AI判断 实现指南

## 思路

**现状**：`Ingredient.java` 写死了分类→保质天数的映射（蔬菜7天、水产5天…），不准确——同一种食材不同储存方式（冷藏/冷冻/常温）保质期差别巨大。

**改法**：把保质期判断逻辑从 Java 代码移到 AI 的 System Prompt 里。用户跟 AI 说"加了鸡蛋，冷藏"，AI 根据 prompt 里的规则表推理出保质天数，调用 `updateExpiryStatus` 工具把数据写入数据库字段。后续查询直接从 DB 字段读，不再靠 Java 硬编码计算。

**流程**：
```
用户: "帮我加鸡蛋，冷藏的"
  → AI 调用 addIngredient("鸡蛋", "蛋类", "冷藏", "2026-08-07")
  → DB 插入食材记录
  → AI 查 prompt 里的规则表 → 鸡蛋+冷藏=30天
  → AI 调用 updateExpiryStatus("鸡蛋", 30)
  → DB 更新 expire_days=30, expire_date=2026-09-06
```

---

## 一、数据库 — ingredient 表加4个字段

```sql
ALTER TABLE ingredient
    ADD COLUMN expire_days    INT          COMMENT '保质天数（AI判定后写入）',
    ADD COLUMN expire_date    DATETIME     COMMENT '过期日期（AI判定后写入）',
    ADD COLUMN storage_method VARCHAR(20)  COMMENT '储存方式：冷藏/冷冻/常温',
    ADD COLUMN purchase_date  DATE         COMMENT '购买日期';
```

---

## 二、Ingredient.java — 删4个方法，加4个字段

**删除** 第42-78行的4个方法：`getExpiryDays()`、`getDaysUntilExpiry()`、`isExpired()`、`isNearExpiry()`

同时删除不再需要的 import：
```java
// 删除这行
import java.time.temporal.ChronoUnit;
```

**新增4个字段**（加在 `createTime` 后面）：
```java
@Schema(description = "储存方式：冷藏/冷冻/常温")
private String storageMethod;

@Schema(description = "购买日期")
private LocalDate purchaseDate;

@Schema(description = "保质天数（AI判定）")
private Integer expireDays;

@Schema(description = "过期日期（purchaseDate + expireDays）")
private LocalDateTime expireDate;
```

---

## 三、AiPrompts.java — AiFridgeFoodService 嵌入保质期规则表

把 `AiFridgeFoodService` 的 prompt（第97-111行）替换为：

```java
case AiFridgeFoodService -> """
    ## AiFridgeFoodService("AI食材管理管家")
    触发条件：用户要求添加食材、查看食材列表、删除食材。
    可用工具：
    - queryMyIngredients：查看冰箱食材
    - addIngredient：添加食材（名称、分类、储存方式、购买日期）
    - deleteIngredient：删除食材
    - updateExpiryStatus：更新食材保质期信息

    ## 保质期规则表（必须严格遵守）
    根据食材分类+储存方式判断保质天数：

    | 分类   | 储存方式 | 保质天数 |
    |--------|---------|---------|
    | 蔬菜   | 冷藏    | 7       |
    | 蔬菜   | 常温    | 3       |
    | 蔬菜   | 冷冻    | 30      |
    | 生禽   | 冷藏    | 3       |
    | 生禽   | 冷冻    | 90      |
    | 蛋类   | 冷藏    | 30      |
    | 蛋类   | 常温    | 15      |
    | 水产   | 冷藏    | 2       |
    | 水产   | 冷冻    | 60      |
    | 豆制品 | 冷藏    | 5       |
    | 豆制品 | 冷冻    | 30      |
    | 其他   | 冷藏    | 7       |
    | 其他   | 常温    | 7       |
    | 其他   | 冷冻    | 30      |

    ## 工作流程（关键！）
    1. 用户说"加XX食材，XX储存方式"
    2. 调用 addIngredient 把食材写入数据库
    3. **立即**查上表，根据分类+储存方式确定保质天数
    4. **立即**调用 updateExpiryStatus 把保质天数写入数据库
    5. 回复用户：已添加XX，保质期XX天，预计X月X日前食用

    限制：
    1. 严禁生成菜谱、烹饪建议，只做食材台账管理
    2. 分类必须是：蔬菜、生禽、蛋类、水产、豆制品、其他
    3. 用户没提供储存方式时，默认"冷藏"
    4. 用户没提供购买日期时，默认当天
    """;
```

---

## 四、IngredientTools.java — 改 addIngredient，加 updateExpiryStatus，改 queryMyIngredients

### 4.1 addIngredient 增加 storageMethod 和 purchaseDate 参数

把原来只有 `name, category` 两个参数的方法替换为：

```java
@Tool(description = "向用户冰箱添加一种新食材。需要食材名称、分类、储存方式（冷藏/冷冻/常温）、购买日期（yyyy-MM-dd）。添加成功后，请根据保质期规则表确定保质天数，然后调用 updateExpiryStatus 写入。")
public String addIngredient(
        @ToolParam(description = "食材名称，如：鸡蛋、西红柿、猪肉") String name,
        @ToolParam(description = "食材分类：蔬菜、生禽、蛋类、水产、豆制品、其他") String category,
        @ToolParam(description = "储存方式：冷藏、冷冻、常温。用户没说则默认冷藏") String storageMethod,
        @ToolParam(description = "购买日期，格式yyyy-MM-dd。用户没说则默认当天") String purchaseDateStr) {
    if (userId == null) return "无法获取用户信息，请先登录。";
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
```

返回值末尾的提示是故意写给 AI 看的——告诉 AI 下一步必须调用 `updateExpiryStatus`。

头部加 import：
```java
import java.time.LocalDate;
```

### 4.2 新增 updateExpiryStatus 方法

加在 `deleteIngredient` 方法后面：

```java
@Tool(description = "根据保质期规则表确定食材的保质天数后，调用此工具将保质期信息写入数据库。必须先调用 addIngredient 成功后再调用。")
public String updateExpiryStatus(
        @ToolParam(description = "食材名称，必须与 addIngredient 时一致") String name,
        @ToolParam(description = "保质天数，根据规则表查到的数字") int expireDays) {
    if (userId == null) return "无法获取用户信息，请先登录。";

    List<Ingredient> list = mapper.selectList(
            new LambdaQueryWrapper<Ingredient>()
                    .eq(Ingredient::getUserId, userId)
                    .eq(Ingredient::getName, name)
                    .isNull(Ingredient::getExpireDays)
                    .orderByDesc(Ingredient::getCreateTime)
    );
    if (list.isEmpty()) return "未找到需要更新保质期的食材：" + name;

    Ingredient ing = list.get(0);
    ing.setExpireDays(expireDays);
    if (ing.getPurchaseDate() != null) {
        ing.setExpireDate(ing.getPurchaseDate().plusDays(expireDays).atStartOfDay());
    }
    mapper.updateById(ing);

    String info = ing.getExpireDate() != null
            ? "，预计" + ing.getExpireDate().toLocalDate() + "前食用" : "";
    return "已更新：" + name + " 保质期 " + expireDays + " 天" + info;
}
```

### 4.3 queryMyIngredients 改用 DB 字段判断状态

把 `queryMyIngredients` 方法体中的状态判断逻辑（`getDaysUntilExpiry()` / `isExpired()` / `isNearExpiry()`）替换为直接从 `expireDate` 字段计算：

```java
@Tool(description = "查询用户冰箱里当前有哪些食材。返回食材名称、储存方式、剩余保质天数。已过期的会标注。")
public String queryMyIngredients() {
    if (userId == null) return "无法获取用户信息，请先登录。";
    List<Ingredient> list = mapper.selectList(
            new LambdaQueryWrapper<Ingredient>().eq(Ingredient::getUserId, userId)
    );
    if (list.isEmpty()) return "冰箱里暂时没有食材记录。";

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
```

需加 import：
```java
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
```

---

## 五、IngredientServiceImpl.java — countNearExpiry 改 SQL，加 countExpired

**countNearExpiry** 替换为 SQL 查询（不再拉全量数据到内存里算）：

```java
@Override
public long countNearExpiry(Long userId) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tomorrow = now.plusDays(1);
    LambdaQueryWrapper<Ingredient> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Ingredient::getUserId, userId)
           .ge(Ingredient::getExpireDate, now)
           .le(Ingredient::getExpireDate, tomorrow);
    return mapper.selectCount(wrapper);
}
```

**新增 countExpired**：

```java
@Override
public long countExpired(Long userId) {
    LambdaQueryWrapper<Ingredient> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Ingredient::getUserId, userId)
           .lt(Ingredient::getExpireDate, LocalDateTime.now());
    return mapper.selectCount(wrapper);
}
```

---

## 六、IIngredientService.java — 接口加 countExpired

```java
long countExpired(Long userId);
```

---

## 七、IngredientController.java — /stats 加 expired

```java
@GetMapping("/stats")
public Map<String, Object> stats(@RequestParam Long userId) {
    long total = service.countByUserId(userId);
    long nearExpiry = service.countNearExpiry(userId);
    long expired = service.countExpired(userId);  // 新增
    Map<String, Object> result = new HashMap<>();
    result.put("total", total);
    result.put("nearExpiry", nearExpiry);
    result.put("expired", expired);  // 新增
    return result;
}
```

---

## 变更文件清单

| 文件 | 操作 |
|------|------|
| `ingredient` 表 | ALTER TABLE 加4个字段 |
| `Ingredient.java` | 删4个方法，加4个字段，删 ChronoUnit import |
| `AiPrompts.java` | AiFridgeFoodService prompt 嵌入保质期规则表+工作流程 |
| `IngredientTools.java` | addIngredient 加参数，新增 updateExpiryStatus，queryMyIngredients 改从 DB 字段读取 |
| `IIngredientService.java` | 加 `countExpired` 方法声明 |
| `IngredientServiceImpl.java` | countNearExpiry 改 SQL 查询，加 countExpired |
| `IngredientController.java` | /stats 加 expired 字段 |

前端不需要改——`/ingredient/list` 接口返回的 Ingredient JSON 会自然带上新增的4个字段（`expireDate` 等），CustomView.vue 的展示逻辑可以后续渐进适配，但当前的 `daysUntilExpiry` 兜底逻辑能兼容 null 值。
