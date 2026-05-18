package com.deepsleep.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /** 设置值并指定过期时间 */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 获取值 */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除 key */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /** 判断 key 是否存在 */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /** 获取 key 的剩余过期时间（秒） */
    public long getExpire(String key) {
        return Optional.ofNullable(
                redisTemplate.getExpire(key, TimeUnit.SECONDS)
        ).orElse(0L);
    }
}
