package com.deepsleep.infrastructure.email.api;

import com.deepsleep.infrastructure.email.model.HtmlEmailMessage;

public interface EmailSender {

    void sendHtmlMessage(String to, HtmlEmailMessage message);
}
