package com.deepsleep;


import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.Teacher;
import com.deepsleep.data.po.User;
import com.deepsleep.mapper.StudentMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.mapper.UserMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;

public class InitTestData {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(
                DeeepsleepStudentManagementBackendApplication.class, args);

        UserMapper userMapper = context.getBean(UserMapper.class);
        StudentMapper studentMapper = context.getBean(StudentMapper.class);
        TeacherMapper teacherMapper = context.getBean(TeacherMapper.class);

        // 管理员
        createUser(userMapper, "admin001", "zhangyuanshuo", 0);
        createUser(userMapper, "admin002", "zhangjinxiao", 0);
        createUser(userMapper, "admin003", "zhaoqingdong", 0);
        createUser(userMapper, "admin004", "zhouzicheng", 0);

        // 教师
        Long teacherUserId = createUser(userMapper, "T20230001", "李老师", 1);
        Teacher teacher = new Teacher();
        teacher.setUserId(teacherUserId);
        teacher.setDeptId(1L);
        teacher.setTitle("讲师");
        teacher.setEntryDate(LocalDate.of(2020, 9, 1));
        teacherMapper.insert(teacher);

        // 学生
        Long studentUserId = createUser(userMapper, "202500550101", "张三", 2);
        Student student = new Student();
        student.setUserId(studentUserId);
        student.setDeptId(1L);
        student.setMajorId(1L);
        student.setClazzId(1L);
        student.setEntryDate(LocalDate.of(2025, 9, 1));
        studentMapper.insert(student);

        System.out.println("测试数据初始化完成");
        context.close();
    }

    private static Long createUser(UserMapper userMapper, String username, String name, int role) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPasswordHash(BCrypt.hashpw("Sdu" + username, BCrypt.gensalt()));
        user.setGender(1);
        user.setRole(role);
        userMapper.insert(user);
        return user.getId();
    }
}