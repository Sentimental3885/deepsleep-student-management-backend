package com.deepsleep.infrastructure.code.email;

import com.deepsleep.config.CodeProperties;
import com.deepsleep.infrastructure.code.store.CodeScene;
import com.deepsleep.infrastructure.code.store.CodeStore;
import com.deepsleep.infrastructure.email.api.EmailSender;
import com.deepsleep.util.VerificationCodeRandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailSender emailSender;
    private final VerificationCodeRandomGenerator randomGenerator;
    private final CodeStore codeStore;
    private final CodeProperties codeProperties;

    @Override
    public void sendVerificationEmail(CodeScene scene, String email, EmailCodeMessageFactory messageFactory) {
        codeStore.setIntervalLock(scene, email);

        try {
            String code = randomGenerator.generateCode();
            codeStore.codeStorage(scene, email, code);

            Integer expireMinutes = Math.toIntExact(codeProperties.codeExpireSeconds() / 60);
            emailSender.sendHtmlMessage(
                    email, messageFactory.create(code, expireMinutes)
            );
        } catch (RuntimeException e) {
            codeStore.releaseIntervalLock(scene, email);
            codeStore.codeDelete(scene, email);
            throw e;
        }
    }

    @Override
    public void checkEmailCode(CodeScene scene, String email, String code) {
        codeStore.codeVerify(scene, email, code);
    }
}
