package com.deepsleep.service;


import com.deepsleep.data.dto.UpdateEmailDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdatePhoneDTO;
import com.deepsleep.data.vo.AvatarUpdateVO;
import com.deepsleep.data.vo.MyUserInfoVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {



    void updateEmail(UpdateEmailDTO dto);
    void updatePhone(UpdatePhoneDTO dto);
    void updatePassword(UpdatePasswordDTO dto);
    AvatarUpdateVO updateAvatar(MultipartFile avatar);
    MyUserInfoVO me();
}
