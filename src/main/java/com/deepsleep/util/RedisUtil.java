package com.deepsleep.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /** 设置值并指定过期时间 */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * set {key} {value} ex {ttl}
     * @param ttl Duration.ofSeconds()/.ofMinutes()/.ofDays()/...
     */
    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * set {key} {value} nx ex {ttl}
     * 只有 key 不存在时才设置成功（原子操作）
     */
    public boolean setIfAbsent(String key, String value, Duration ttl) {
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        return flag != null && flag;
    }

    /** 获取值 */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除 key */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /** 判断 key 是否存在 */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    private static final DefaultRedisScript<Long> INCR_WITH_TTL_LUA = new DefaultRedisScript<>(
            """
                local v = redis.call('INCR', KEYS[1])
                if v == 1 then
                  redis.call('EXPIRE', KEYS[1], ARGV[1])
                end
                return v
            """,
            Long.class
    );

    public long incrWithTtl(String key, Duration ttl) {
        return redisTemplate.execute(
                INCR_WITH_TTL_LUA, List.of(key), String.valueOf(ttl.toSeconds())
        );
    }

    /** 获取 key 的剩余过期时间（秒） */
    public long getExpire(String key) {
        return Optional.of(
                redisTemplate.getExpire(key, TimeUnit.SECONDS)
        ).orElse(0L);
    }
}
