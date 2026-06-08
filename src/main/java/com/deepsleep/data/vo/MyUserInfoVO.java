package com.deepsleep.data.vo;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyUserInfoVO(
        Long id,
        String username,
        String name,
        String avatar,
        String phone,
        String email,
        Integer gender,
        Integer role,
        LocalDateTime createTime
) {
}
