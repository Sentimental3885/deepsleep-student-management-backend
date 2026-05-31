package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.AddCourseDTO;
import com.deepsleep.data.dto.ScheduleDTO;
import com.deepsleep.data.dto.UpdateCourseDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<CourseVO> detailCourse(@PathVariable Long cid){
        return courseService.getDetail(cid);
    }

    /**
     * 删除课程
     * @param cid 课程id
     */
    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/delete/{cid}")
    public Result<Void> deleteCourse(@PathVariable Long cid){
        return courseService.deleteCourse(cid);
    }

    /**
     * 修改课程信息
     * @param cid 课程id
     * @param updateCourseDTO 课程详情
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @PutMapping("/update/{cid}")
    public Result<Void> updateCourse(@PathVariable Long cid, @RequestBody @Valid UpdateCourseDTO updateCourseDTO){
        if (RoleEnum.fromCode(UserContext.getRole()) == RoleEnum.TEACHER){
            courseService.verifyTeacher(UserContext.getUserId(), cid);
        }
        return courseService.updateCourse(cid, updateCourseDTO);
    }

    /**
     * 获取单课程的课程表
     * @param cid 课程id
     */
    @RequireLogin
    @GetMapping("/schedule/{cid}")
    public Result<List<ScheduleVO>> getScheduleByCourse(@PathVariable Long cid){
        return courseService.getScheduleByCourse(cid);
    }

    /**
     * 增加排课
     * @param cid 课程id
     * @param scheduleDTO 排课信息
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @PostMapping("/schedule/{cid}")
    public Result<Void> addSchedule(@PathVariable Long cid, @RequestBody @Valid ScheduleDTO scheduleDTO){
        if (RoleEnum.fromCode(UserContext.getRole()) == RoleEnum.TEACHER){
            courseService.verifyTeacher(UserContext.getUserId(), cid);
        }
        return courseService.addSchedule(cid, scheduleDTO);
    }

    /**
     * 删除排课
     * @param cid 课程id，须一并上传用于验证
     * @param scid 排课id
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @DeleteMapping("/schedule/{cid}/{scid}")
    public Result<Void> deleteSchedule(@PathVariable Long cid, @PathVariable Long scid){
        if (RoleEnum.fromCode(UserContext.getRole()) == RoleEnum.TEACHER){
            courseService.verifyTeacher(UserContext.getUserId(), cid);
        }
        return courseService.deleteSchedule(cid, scid);
    }

    /**
     * 更改排课
     * @param cid 课程id，须一并上传用于验证
     * @param scid 排课id
     * @param scheduleDTO 排课信息
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @PutMapping("/schedule/{cid}/{scid}")
    public Result<Void> updateSchedule(@PathVariable Long cid, @PathVariable Long scid,
                                       @RequestBody @Valid ScheduleDTO scheduleDTO){
        if (RoleEnum.fromCode(UserContext.getRole()) == RoleEnum.TEACHER){
            courseService.verifyTeacher(UserContext.getUserId(), cid);
        }
        return courseService.updateSchedule(cid, scid, scheduleDTO);
    }
}
