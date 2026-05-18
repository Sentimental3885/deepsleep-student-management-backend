package com.deepsleep;


import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.mapper.StudentMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class InitTestDataTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    public void initData() {
        // 管理员账号
        createUser("admin001", "zhangyuanshuo", 0);
        createUser("admin002", "zhangjinxiao", 0);
        createUser("admin003", "zhaoqingdong", 0);
        createUser("admin004", "zhouzicheng", 0);


        Long teacherUserId = createUser("T20230001", "", 1);
        Teacher teacher = new Teacher();
        teacher.setUserId(teacherUserId);
        teacher.setDeptId(1L);
        teacher.setTitle("讲师");
        teacher.setEntryDate(LocalDate.of(2020, 9, 1));
        teacherMapper.insert(teacher);

        Long studentUserId = createUser("202500550101", "张三", 2);
        Student student = new Student();
        student.setUserId(studentUserId);
        student.setDeptId(1L);
        student.setMajorId(1L);
        student.setClazzId(1L);
        student.setEntryDate(LocalDate.of(2025, 9, 1));
        studentMapper.insert(student);
    }

    private Long createUser(String username, String name, int role) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        // 初始密码统一为Sdu+用户名
        user.setPasswordHash(BCrypt.hashpw("Sdu" + username, BCrypt.gensalt()));
        user.setGender(1);
        user.setRole(role);
        userMapper.insert(user);
        return user.getId();
    }
}
