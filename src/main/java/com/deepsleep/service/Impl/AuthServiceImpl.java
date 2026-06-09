package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.LoginDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.LoginVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.AuthService;
import com.deepsleep.util.JwtUtil;
import com.deepsleep.util.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final FileStorage fileStorage;

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

        return new LoginVO(token, user.getName(), fileStorage.getUrl(user.getAvatar()), user.getRole());

    }

    @Override
    public void logout(String token) {
        // 拿出剩余时间
        Claims claims = jwtUtil.parseToken(token);

        long expiration = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();
        long ttl = (expiration-now)/1000;

        if(ttl>0){// 设置黑名单时长与token剩余有效时长一致
            redisUtil.set("blacklist:" + token, "1", ttl, TimeUnit.SECONDS);
        }
    }
}
