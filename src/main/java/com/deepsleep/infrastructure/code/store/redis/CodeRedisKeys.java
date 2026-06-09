package com.deepsleep.infrastructure.code.store.redis;


import com.deepsleep.infrastructure.code.store.CodeScene;

public class CodeRedisKeys {

    /**
     * 项目前缀，deepsleep-blog缩写
     */
    private static final String APP = "db";

    /**
     * 分隔符
     */
    private static final String SEP = ":";

    /**
     * 通用 key 拼接器
     */
    private static String build(String ... parts) {
        StringBuilder sb = new StringBuilder(APP);
        for (String p : parts) {
            if (p == null || p.isBlank()) {
                throw new IllegalArgumentException("Redis key的组成部分不能为空！");
            }
            sb.append(SEP).append(p);
        }
        return sb.toString();
    }

    /**
     * 验证码key 格式：db:{channel}:{sceneName}:code:{targetHash}
     * @param targetHash 哈希加密的手机号或邮箱
     */
    public static String codeKey(CodeScene scene, String targetHash) {
        return build(scene.channel(), scene.sceneName(), "code", targetHash);
    }

    /**
     * 发送间隔锁key 格式：db:{channel}:{sceneName}:interval:{targetHash}
     */
    public static String intervalKey(CodeScene scene, String targetHash) {
        return build(scene.channel(), scene.sceneName(), "interval", targetHash);
    }

    /**
     * 失败次数锁key 格式：db:{channel}:{sceneName}:fail:{targetHash}
     */
    public static String failKey(CodeScene scene, String targetHash) {
        return build(scene.channel(), scene.sceneName(), "fail", targetHash);
    }
}
