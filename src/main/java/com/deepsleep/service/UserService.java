package com.deepsleep.service;

import com.deepsleep.data.dto.UpdateContactDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.vo.UserProfileVO;

public interface UserService {

    UserProfileVO getProfile();

    void updateContact(UpdateContactDTO dto);
    void updatePassword(UpdatePasswordDTO dto);
    void updateStudentInfo(UpdateStudentDTO dto);
}
