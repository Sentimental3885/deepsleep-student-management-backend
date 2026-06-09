package com.deepsleep.infrastructure.code.store.redis;

import com.deepsleep.config.CodeProperties;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.code.store.CodeScene;
import com.deepsleep.infrastructure.code.store.CodeStore;
import com.deepsleep.util.HashUtils;
import com.deepsleep.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisCodeStore implements CodeStore {

    private final RedisUtil redis;
    private final CodeProperties codeProperties;

    @Override
    public void setIntervalLock(CodeScene scene, String target) {
        boolean locked =  redis.setIfAbsent(
                CodeRedisKeys.intervalKey(scene, targetHash(target)),
                "a",
                Duration.ofSeconds(codeProperties.intervalLockExpireSeconds())
        );

        if (!locked) throw new BusinessException(ResultCode.CODE_SEND_TOO_FREQUENTLY);
    }

    @Override
    public void releaseIntervalLock(CodeScene scene, String target) {
        redis.delete(CodeRedisKeys.intervalKey(scene, targetHash(target)));
    }

    @Override
    public void codeStorage(CodeScene scene, String target, String code) {
        redis.set(
                CodeRedisKeys.codeKey(scene, targetHash(target)),
                code,
                Duration.ofSeconds(codeProperties.codeExpireSeconds())
        );
    }

    @Override
    public void codeVerify(CodeScene scene, String target, String code) {
        String targetHash = targetHash(target);
        String correctCode = redis.get(CodeRedisKeys.codeKey(scene, targetHash));

        if (correctCode == null) {
            throw new BusinessException(ResultCode.CODE_NOT_EXISTS);
        }

        if (!correctCode.equals(code)) {
            recordFailAttempt(scene, target);
            throw new BusinessException(ResultCode.CODE_INCORRECT);
        }

        redis.delete(CodeRedisKeys.codeKey(scene, targetHash));
        redis.delete(CodeRedisKeys.failKey(scene, targetHash));
    }

    /**
     * 记录失败次数，达到最大失败次数后抛出异常
     * @param scene 业务场景
     * @param target 用户手机号或邮箱
     */
    void recordFailAttempt(CodeScene scene, String target) {
        String targetHash = targetHash(target);

        long fail = redis.incrWithTtl(
                CodeRedisKeys.failKey(scene, targetHash),
                Duration.ofSeconds(codeProperties.codeExpireSeconds())
        );

        if (fail >= codeProperties.maxFailNumber()) {
            redis.delete(CodeRedisKeys.codeKey(scene, targetHash));
            redis.delete(CodeRedisKeys.failKey(scene, targetHash));
            throw new BusinessException(ResultCode.CODE_FAILED_ATTEMPTS_TOO_MUCH);
        }
    }

    @Override
    public void codeDelete(CodeScene scene, String target) {
        redis.delete(CodeRedisKeys.codeKey(scene, targetHash(target)));
    }

    /**
     * 手机号或邮箱哈希摘要，用于组成redis key
     */
    private String targetHash(String target) {
        return HashUtils.hmacSha256Hex(target, codeProperties.targetHashSecret());
    }
}
