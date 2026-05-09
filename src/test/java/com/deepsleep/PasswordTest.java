package com.deepsleep;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordTest {

    @Test
    public void generatePassword(){
        /*
        学生202500550001
        String password = "Sdu20250001";
        String hash = BCrypt.hashpw(password,BCrypt.gensalt());
        System.out.println(hash);
        */
        //教师19850001
        String password = "Sdu19850001";
        String hash = BCrypt.hashpw(password,BCrypt.gensalt());
        System.out.println(hash);

    }
}
