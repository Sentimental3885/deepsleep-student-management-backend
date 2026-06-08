package com.deepsleep.email.api.impl;


import com.deepsleep.config.EmailProperties;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.email.api.EmailSender;
import com.deepsleep.email.model.HtmlEmailMessage;
import com.deepsleep.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    private static final boolean DISABLE_MULTIPART_MESSAGE = false;

    private static final boolean ENABLE_HTML_CONTENT = true;

    @Override
    public void sendHtmlMessage(String to, HtmlEmailMessage message) {
        if (!isValid(to)) {
            throw new BusinessException(ResultCode.EMAIL_SEND_FAILED, "收信人邮箱为空或格式不合法");
        }

        String subject = message.subject();
        String html = message.htmlContent();
        if (subject == null || subject.isBlank()) {
            throw new BusinessException(ResultCode.EMAIL_SEND_FAILED, "邮件主题不能为空");
        }
        if (html == null || html.isBlank()) {
            throw new BusinessException(ResultCode.EMAIL_SEND_FAILED, "邮件内容不能为空");
        }

        String normalizedTo = to.trim();

        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mailMessage, DISABLE_MULTIPART_MESSAGE, StandardCharsets.UTF_8.name()
            );

            helper.setFrom(emailProperties.from());
            helper.setTo(normalizedTo);
            helper.setSubject(subject);
            helper.setText(html, ENABLE_HTML_CONTENT);

            mailSender.send(mailMessage);

        } catch (MessagingException | MailException e) {
            throw new BusinessException(ResultCode.EMAIL_SEND_FAILED);
        }
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,254}$)" +
                    "(?=.{1,64}@)" +
                    "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+" +
                    "(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                    "@" +
                    "(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+" +
                    "[A-Za-z]{2,63}$"
    );

    private boolean isValid(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}
