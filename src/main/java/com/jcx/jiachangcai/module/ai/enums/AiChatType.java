package com.jcx.jiachangcai.module.ai.enums;

public enum AiChatType {
    CHEF("AI菜谱专家"),
    CustomizedRecipe("定制食谱"),//根据特殊需求客制化菜谱
    Oneclickmenu("一键菜谱"),//提供我有什么菜，根据提供菜生成菜谱
    AiFridgeFoodService("AI食材管理管家"),//仅限管理用户添加有什么菜 什么时候买的
    CUSTOMER_SERVICE("电商智能客服");
    private final String displayName;

    AiChatType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
