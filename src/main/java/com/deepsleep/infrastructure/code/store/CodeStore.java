package com.deepsleep.infrastructure.code.store;

public interface CodeStore {

    /**
     * 设置验证码间隔锁，若间隔锁已存在则抛出异常
     * @param scene 业务场景
     * @param target 用户手机号或邮箱
     */
    void setIntervalLock(CodeScene scene, String target);

    /**
     * 释放间隔锁
     * @param scene 业务场景
     * @param target 用户手机号或邮箱
     */
    void releaseIntervalLock(CodeScene scene, String target);

    /**
     * 存储验证码至redis
     * @param scene 业务场景
     * @param target 用户手机号或邮箱
     * @param code 验证码
     */
    void codeStorage(CodeScene scene, String target, String code);

    /**
     * 验证码校验
     * @param scene 业务场景
     * @param target 用户手机号或邮箱
     * @param code 验证码
     */
    void codeVerify(CodeScene scene, String target, String code);

    /**
     * 残留验证码删除
     * @param scene 业务场景
     * @param target 用户手机号或邮箱
     */
    void codeDelete(CodeScene scene, String target);
}
