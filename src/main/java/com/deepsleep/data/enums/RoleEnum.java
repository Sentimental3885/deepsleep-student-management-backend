package com.deepsleep.data.enums;

import com.deepsleep.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(0), TEACHER(1), STUDENT(2);

    private final int code;

    public static RoleEnum fromCode(int code){
        for(RoleEnum role: values()){
            if (role.code==code) return role;
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED);
    }
}
