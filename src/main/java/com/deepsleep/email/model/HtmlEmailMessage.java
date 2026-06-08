package com.deepsleep.email.model;

public record HtmlEmailMessage(
        String subject,
        String htmlContent
) {
}
