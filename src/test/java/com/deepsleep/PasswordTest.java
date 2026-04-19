package com.deepsleep;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordTest {

    @Test
    public void generatePassword(){
        String password = "Sdu20250001";
        String hash = BCrypt.hashpw(password,BCrypt.gensalt());
        System.out.println(hash);
    }
}
