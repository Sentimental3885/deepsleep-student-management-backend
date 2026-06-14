package com.deepsleep.infrastructure.code.store;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CodeScene {

    UPDATE_EMAIL(Channel.email, "updateEmail"),
    UPDATE_PASSWORD(Channel.email, "updatePassword"),

    UPDATE_PHONE(Channel.sms, "updatePhone")
    ;

    private final Channel channel;
    private final String sceneName;

    public String channel() {
        return channel.name();
    }

    public String sceneName() {
        return sceneName;
    }

    enum Channel {
        sms, email
    }
}
