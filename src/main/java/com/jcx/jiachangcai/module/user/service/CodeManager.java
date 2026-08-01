package com.jcx.jiachangcai.module.user.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码内存存储，5分钟过期
 */
@Component
public class CodeManager {

    private final ConcurrentHashMap<String, CodeInfo> store = new ConcurrentHashMap<>();

    private static final long TTL_MS = 5 * 60 * 1000;

    private static class CodeInfo {
        final String code;
        final long expireAt;
        CodeInfo(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    public void save(String key, String code) {
        store.put(key, new CodeInfo(code, System.currentTimeMillis() + TTL_MS));
    }

    public boolean verify(String key, String code) {
        CodeInfo info = store.get(key);
        if (info == null) return false;
        if (System.currentTimeMillis() > info.expireAt) {
            store.remove(key);
            return false;
        }
        if (info.code.equals(code)) {
            store.remove(key);
            return true;
        }
        return false;
    }
}
