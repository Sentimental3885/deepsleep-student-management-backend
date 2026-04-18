package com.deepsleep.service;

import com.deepsleep.data.dto.LoginDTO;
import com.deepsleep.data.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);
}