package com.deepsleep.infrastructure.code.email;

import com.deepsleep.infrastructure.code.store.CodeScene;

public interface EmailVerificationService {
    void sendVerificationEmail(CodeScene scene, String email, EmailCodeMessageFactory messageFactory);

    void checkEmailCode(CodeScene scene, String email, String code);
}
