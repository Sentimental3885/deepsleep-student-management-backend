package com.deepsleep.infrastructure.email.model;

public record HtmlEmailMessage(
        String subject,
        String htmlContent
) {
}
