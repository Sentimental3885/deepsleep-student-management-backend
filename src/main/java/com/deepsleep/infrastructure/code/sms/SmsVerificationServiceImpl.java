package com.deepsleep.infrastructure.code.sms;

import com.deepsleep.infrastructure.code.store.CodeScene;
import com.deepsleep.infrastructure.code.store.CodeStore;
import com.deepsleep.infrastructure.sms.SmsSender;
import com.deepsleep.util.VerificationCodeRandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsVerificationServiceImpl implements SmsVerificationService{

    private final SmsSender smsSender;
    private final VerificationCodeRandomGenerator randomGenerator;
    private final CodeStore codeStore;


    @Override
    public void sendSmsCode(CodeScene scene, String phone) {
        codeStore.setIntervalLock(scene, phone);

        try {
            String code = randomGenerator.generateCode();
            codeStore.codeStorage(scene, phone, code);

            smsSender.send(phone, code);
        } catch (RuntimeException e) {
            codeStore.releaseIntervalLock(scene, phone);
            codeStore.codeDelete(scene, phone);
            throw e;
        }
    }

    @Override
    public void checkSmsCode(CodeScene scene, String phone, String code) {
        codeStore.codeVerify(scene, phone, code);
    }
}
