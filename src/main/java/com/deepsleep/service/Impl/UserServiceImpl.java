package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.SendCodeDTO;
import com.deepsleep.data.dto.UpdateEmailDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdatePhoneDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.AvatarUpdateVO;
import com.deepsleep.data.vo.MyUserInfoVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.code.email.EmailVerificationService;
import com.deepsleep.infrastructure.code.store.CodeScene;
import com.deepsleep.infrastructure.email.model.MailFactory;
import com.deepsleep.infrastructure.file.model.UploadFile;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final EmailVerificationService emailVerificationService;
    private final MailFactory mailFactory;
    private final FileStorage fileStorage;

    @Override
    public void updateEmailCode(SendCodeDTO sendCodeDTO) {
        // 如邮箱已被其他用户（包括用户自己）绑定，则不能重复绑定。
        boolean exists = userMapper.exists(
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, sendCodeDTO.getEmail())
        );
        if (exists) throw new BusinessException(ResultCode.EMAIL_CONFLICT);

        emailVerificationService.sendVerificationEmail(
                CodeScene.UPDATE_EMAIL,
                sendCodeDTO.getEmail(),
                (code, expireMinutes) -> mailFactory.createUpdateEmailMail(
                        sendCodeDTO.getEmail(), code, expireMinutes, LocalDateTime.now()
                )
        );
    }

    @Override
    public void updateEmail(UpdateEmailDTO dto) {
        Long userId = UserContext.getUserId();
        // 先验证验证码
        emailVerificationService.checkEmailCode(CodeScene.UPDATE_EMAIL, dto.getEmail(), dto.getCode());

        User newUser = new User();
        newUser.setId(userId);
        newUser.setEmail(dto.getEmail());
        userMapper.updateById(newUser);
    }

    @Override
    public void updatePhone(UpdatePhoneDTO dto) {
//        Long userId = UserContext.getUserId();
//        // 取邮箱校验验证码
//        User user = userMapper.selectById(userId);
//        if (user.getEmail() == null) throw new BusinessException(ResultCode.EMAIL_NOT_BOUND);
//        emailService.verifyCode(user.getEmail(), dto.getCode());
//        // 检查手机号是否被占用
//        Long count = userMapper.selectCount(
//                new LambdaQueryWrapper<User>()
//                        .eq(User::getPhone, dto.getPhone())
//                        .ne(User::getId, userId)
//        );
//        if (count > 0) throw new BusinessException(ResultCode.PHONE_CONFLICTED);
//
//        User update = new User();
//        update.setId(userId);
//        update.setPhone(dto.getPhone());
//        userMapper.updateById(update);
    }

    @Override
    public void updatePasswordCode() {
        String userEmail = userMapper.selectEmailById(UserContext.getUserId());
        if (userEmail == null) throw new BusinessException(ResultCode.EMAIL_NOT_BOUND);

        emailVerificationService.sendVerificationEmail(
                CodeScene.UPDATE_PASSWORD,
                userEmail,
                (code, expireMinutes) -> mailFactory.createUpdatePasswordMail(
                        userEmail, code, expireMinutes, LocalDateTime.now()
                )
        );
    }

    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        String userEmail = userMapper.selectEmailById(UserContext.getUserId());
        if (userEmail == null) throw new BusinessException(ResultCode.EMAIL_NOT_BOUND);

        emailVerificationService.checkEmailCode(
                CodeScene.UPDATE_PASSWORD, userEmail, dto.getCode()
        );

        Long userId = UserContext.getUserId();
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

        if (avatar == null || avatar.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_EMPTY);
        }

        LegalAvatarType avatarType = validate(avatar);
        Long userId = UserContext.getUserId();
        String objectKey = generateImageKey(userId, avatarType);

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

        int updated = userMapper.updateAvatarById(userId, objectKey);
        if (updated <= 0) {
            fileStorage.deleteQuietly(objectKey);
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

    @Override
    public MyUserInfoVO me() {
        User me = userMapper.selectById(UserContext.getUserId());
        if (me == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return MyUserInfoVO.builder()
                .id(me.getId())
                .username(me.getUsername())
                .name(me.getName())
                .avatar(fileStorage.getUrl(me.getAvatar()))
                .phone(me.getPhone())
                .email(me.getEmail())
                .gender(me.getGender())
                .role(me.getRole())
                .createTime(me.getCreateTime())
                .build();
    }

}
