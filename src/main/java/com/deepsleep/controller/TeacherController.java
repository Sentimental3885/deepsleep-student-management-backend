package com.deepsleep.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.TeacherOptionQueryDTO;
import com.deepsleep.data.dto.UpdateTeacherDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.ExamVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.TeacherCourseVO;
import com.deepsleep.data.vo.TeacherOptionVO;
import com.deepsleep.data.vo.TeacherProfileVO;
import com.deepsleep.service.ExamService;
import com.deepsleep.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final ExamService examService;

    /**
     * 教师选择器
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @GetMapping("/options")
    public Result<IPage<TeacherOptionVO>> getTeacherOptions(@ModelAttribute @Valid TeacherOptionQueryDTO dto) {
        dto.setDefaultValue();
        return Result.success(teacherService.getTeacherOptions(dto));
    }

    /**
     * 查看教师个人信息
     */
    @RequireRole(RoleEnum.TEACHER)
    @GetMapping("/profile")
    public Result<TeacherProfileVO> getTeacherProfile() {
        return Result.success(teacherService.getTeacherProfile());
    }

    /**
     * 更新教师个人信息
     */
    @RequireRole(RoleEnum.TEACHER)
    @PutMapping("/profile")
    public Result<Void> updateTeacherInfo(@RequestBody UpdateTeacherDTO dto){
        teacherService.updateTeacherInfo(dto);
        return Result.success();
    }

    /**
     * 教师查看自己的课程列表
     * @return 课程详情list
     */
    @RequireRole(RoleEnum.TEACHER)
    @GetMapping("/courses")
    public Result<List<TeacherCourseVO>> getMyCourses(){
        return Result.success(teacherService.getMyCourses());
    }

    /**
     * 教师查看自己的授课课表
     */
    @RequireRole(RoleEnum.TEACHER)
    @GetMapping("/schedule")
    public Result<List<ScheduleVO>> getMySchedule() {
        return Result.success(teacherService.getMySchedule());
    }

    /**
     * 教师查看自己监考的考试列表
     * @return 考试List
     */
    @RequireRole(RoleEnum.TEACHER)
    @GetMapping("/exams")
    public Result<List<ExamVO>> getMyExams() { return Result.success(examService.getMyExamsAsInvigilator());}
}
