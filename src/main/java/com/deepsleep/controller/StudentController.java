package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.StudentProfileVO;
import com.deepsleep.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生特有操作：如查看学生个人信息，查看课表等
 */
@RequestMapping("/student")
@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 获取学生个人信息
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/profile")
    public Result<StudentProfileVO> getStudentProfile() {
        return Result.success(studentService.getStudentProfile());
    }

    /**
     * 更新学生个人信息
     */
    @RequireRole(RoleEnum.STUDENT)
    @PutMapping("/profile")
    public Result<Void> updateStudentInfo(@RequestBody UpdateStudentDTO dto){
        studentService.updateStudentInfo(dto);
        return Result.success();
    }

    /**
     * 学生获取课表
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/schedule")
    public Result<List<ScheduleVO>> getMySchedule(){
        return  Result.success(studentService.getMySchedule());
    }
}
