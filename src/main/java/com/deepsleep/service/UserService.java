package com.deepsleep.service;


import com.deepsleep.data.dto.UpdateEmailDTO;
import com.deepsleep.data.dto.UpdatePasswordDTO;
import com.deepsleep.data.dto.UpdatePhoneDTO;

public interface UserService {



    void updateEmail(UpdateEmailDTO dto);
    void updatePhone(UpdatePhoneDTO dto);
    void updatePassword(UpdatePasswordDTO dto);
}
