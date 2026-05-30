package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.AddCourseDTO;
import com.deepsleep.data.dto.UpdateCourseDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/course")
@RestController
public class CourseController {

    @Resource
    private CourseService courseService;

    /**
     * 增加课程
     * @param addCourseDTO 课程详情+开课班级
     */
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/add")
    public Result<Void> addCourse(@RequestBody @Valid AddCourseDTO addCourseDTO){
        return courseService.addCourse(addCourseDTO);
    }

    /**
     * 课程详情
     * @param cid 课程id
     */
    @RequireLogin
    @GetMapping("/detail/{cid}")
    public Result<CourseVO> detailCourse(@PathVariable @NotNull Long cid){
        return courseService.getDetail(cid);
    }

    /**
     * 删除课程
     * @param cid 课程id
     */
    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/delete/{cid}")
    public Result<Void> deleteCourse(@PathVariable @NotNull Long cid){
        return courseService.deleteCourse(cid);
    }

    /**
     * 修改课程信息
     * @param cid 课程id
     * @param updateCourseDTO 课程详情
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @PutMapping("/update/{cid}")
    public Result<Void> updateCourse(@PathVariable @NotNull Long cid, @RequestBody @Valid UpdateCourseDTO updateCourseDTO){
        if (RoleEnum.fromCode(UserContext.getRole()) == RoleEnum.TEACHER){
            courseService.verifyTeacher(UserContext.getUserId(), cid);
        }
        return courseService.updateCourse(cid, updateCourseDTO);
    }
}
