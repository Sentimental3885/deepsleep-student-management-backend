package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateEmailDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdatePhoneDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.AvatarUpdateVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.file.model.UploadFile;
import com.deepsleep.file.storage.FileStorage;
import com.deepsleep.mapper.*;
import com.deepsleep.service.EmailService;
import com.deepsleep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final EmailService emailService;
    private final FileStorage fileStorage;

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

    /**
     * OSS Keys 标准分隔符
     */
    private static final String SEP = "/";
    /**
     * 后缀名点
     */
    private static final String DOT = ".";
    /**
     * 头像业务代号
     */
    private static final String BIZ_NAME = "avatars";

    /**
     * 生成OSS key，格式：{bizName}/{userId}/{yyyyMMdd}/{uuid}.{ext}
     * @param userId 用户 id
     * @param avatarType 头像文件类型
     */
    private String generateImageKey(Long userId, LegalAvatarType avatarType) {
        LocalDate d = LocalDate.now();
        String datePath = String.format("%s%04d%02d%02d%s",
                SEP, d.getYear(), d.getMonthValue(), d.getDayOfMonth(), SEP);

        return BIZ_NAME + SEP + userId + datePath + java.util.UUID.randomUUID() + DOT + avatarType.getExtension();
    }

    @Override
    public AvatarUpdateVO updateAvatar(MultipartFile avatar) {

        LegalAvatarType avatarType = validate(avatar);
        String objectKey = generateImageKey(UserContext.getUserId(), avatarType);

        try (InputStream inputStream = avatar.getInputStream()) {

            UploadFile uploadFile = UploadFile.builder()
                    .objectKey(objectKey)
                    .contentType(avatarType.getContentType())
                    .size(avatar.getSize())
                    .inputStream(inputStream)
                    .build();

            fileStorage.storage(uploadFile);

        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }

        String avatarUrl = fileStorage.getUrl(objectKey);
        return new AvatarUpdateVO(objectKey, avatarUrl);
    }

    private LegalAvatarType validate(MultipartFile avatar) {
        try (InputStream inputStream = avatar.getInputStream()) {
            return LegalAvatarType.detectImageType(inputStream);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
    }

}
