package com.deepsleep.email.api;

import com.deepsleep.email.model.HtmlEmailMessage;

public interface EmailSender {

    void sendHtmlMessage(String to, HtmlEmailMessage message);
}
