package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateEmailDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdatePhoneDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.User;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.*;
import com.deepsleep.service.EmailService;
import com.deepsleep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final EmailService emailService;


    @Override
    public void updateEmail(UpdateEmailDTO dto) {
        Long userId = UserContext.getUserId();
        // 先验证验证码
        emailService.verifyCode(dto.getEmail(), dto.getCode());
        // 邮箱是否被占用
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, dto.getEmail())
                        .ne(User::getId, userId)
        );
        if (count > 0) throw new BusinessException(ResultCode.EMAIL_CONFLICT);

        User newUser = new User();
        newUser.setId(userId);
        newUser.setEmail(dto.getEmail());
        userMapper.updateById(newUser);
    }

    @Override
    public void updatePhone(UpdatePhoneDTO dto) {
        Long userId = UserContext.getUserId();
        // 取邮箱校验验证码
        User user = userMapper.selectById(userId);
        if (user.getEmail() == null) throw new BusinessException(ResultCode.EMAIL_NOT_BOUND);
        emailService.verifyCode(user.getEmail(), dto.getCode());
        // 检查手机号是否被占用
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, dto.getPhone())
                        .ne(User::getId, userId)
        );
        if (count > 0) throw new BusinessException(ResultCode.PHONE_CONFLICTED);

        User update = new User();
        update.setId(userId);
        update.setPhone(dto.getPhone());
        userMapper.updateById(update);
    }



    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getEmail() == null) throw new BusinessException(ResultCode.EMAIL_NOT_BOUND);
        emailService.verifyCode(user.getEmail(),dto.getCode());

        User update = new User();
        update.setId(userId);
        update.setPasswordHash(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        userMapper.updateById(update);
    }

}
