package com.jcx.jiachangcai.module.ai.enums;

public enum AiChatType {
    CHEF("饮食管家"),
    RECIPE("定制食谱"),
    MENU("一键菜单");

    private final String displayName;

    AiChatType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
