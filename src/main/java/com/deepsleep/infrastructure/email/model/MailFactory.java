package com.deepsleep.infrastructure.email.model;

import com.deepsleep.infrastructure.email.api.EmailTemplateRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class MailFactory {

    private final EmailTemplateRenderer renderer;

    private static final String UPDATE_EMAIL_TEMPLATE_NAME = "update-email";
    private static final String UPDATE_EMAIL_SUBJECT = "【DeepSleep学生管理系统】 邮箱改绑验证码";

    private static final String UPDATE_PASSWORD_TEMPLATE_NAME = "update-password";
    private static final String UPDATE_PASSWORD_SUBJECT = "【DeepSleep学生管理系统】 密码修改验证码";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HtmlEmailMessage createUpdateEmailMail(String recipientEmail, String code, Integer expireMinutes, LocalDateTime operationTime) {
        UpdatePersonalInfoMailModel updateEmailModel = new UpdatePersonalInfoMailModel(
                recipientEmail, code, expireMinutes,
                operationTime.format(dtf),
                Year.now().getValue()
        );

        String html = renderer.render(UPDATE_EMAIL_TEMPLATE_NAME, updateEmailModel);
        return new HtmlEmailMessage(UPDATE_EMAIL_SUBJECT, html);
    }

    public HtmlEmailMessage createUpdatePasswordMail(String recipientEmail, String code, Integer expireMinutes, LocalDateTime operationTime) {
        UpdatePersonalInfoMailModel updatePasswordModel = new UpdatePersonalInfoMailModel(
                recipientEmail, code, expireMinutes,
                operationTime.format(dtf),
                Year.now().getValue()
        );

        String html = renderer.render(UPDATE_PASSWORD_TEMPLATE_NAME, updatePasswordModel);
        return new HtmlEmailMessage(UPDATE_PASSWORD_SUBJECT, html);
    }
}
