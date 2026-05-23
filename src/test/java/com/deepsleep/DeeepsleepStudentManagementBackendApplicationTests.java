package com.deepsleep;


import com.deepsleep.service.StudentService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeeepsleepStudentManagementBackendApplicationTests {

    @Resource
    StudentService studentService;
    @Test
    void contextLoads() {
//        AddStudentDTO dto = new AddStudentDTO();
//        dto.setSsid(123456L);
//        dto.setName("ZZZZZZZ");
//        dto.setDid(123456L);
//        dto.setMid(123456L);
//        dto.setZid(123456L);
//        studentService.addStudent(dto);
    }

}
