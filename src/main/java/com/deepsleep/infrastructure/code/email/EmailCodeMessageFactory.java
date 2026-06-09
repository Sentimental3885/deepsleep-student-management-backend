package com.deepsleep.infrastructure.code.email;

import com.deepsleep.infrastructure.email.model.HtmlEmailMessage;

@FunctionalInterface
public interface EmailCodeMessageFactory {
    HtmlEmailMessage create(String code, Integer expireMinutes);
}
