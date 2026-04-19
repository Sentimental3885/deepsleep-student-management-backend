package com.deepsleep.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.LoginDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.LoginVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;


    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername,dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        return new LoginVO(token,user.getName(),user.getRole());

    }
}
