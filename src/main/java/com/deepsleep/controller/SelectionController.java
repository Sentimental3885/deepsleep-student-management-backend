package com.deepsleep.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.CourseStudentVO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.SelectionVO;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/selection")
public class SelectionController {

    @Resource
    private SelectionService selectionService;

    /**
     * 获取可选课程列表
     * @param dto 查询参数，支持null和仅传部分参数
     * @return 分页课程列表
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/courseList")
    public Result<IPage<CourseVO>> showCourseList(@Valid SelectionQueryDTO dto) {
        dto.setDefaultValue();
        return selectionService.showAvailableList(UserContext.getUserId(), dto);
    }

    /**
     * 选课
     * @param cid 课程id
     */
    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/pick/{cid}")
    public Result<Void> pickCourse(@PathVariable Long cid){
        return selectionService.pickCourse(UserContext.getUserId(), cid);
    }

    /**
     * 退课
     * @param cid 课程id
     */
    @RequireRole(RoleEnum.STUDENT)
    @DeleteMapping("/drop/{cid}")
    public Result<Void> dropCourse(@PathVariable Long cid){
        return selectionService.dropCourse(UserContext.getUserId(), cid);
    }

    /**
     * 结课
     * @param dto 结课信息
     */
    @RequireRole(RoleEnum.TEACHER)
    @PutMapping("/end")
    public Result<Void> endCourse(@RequestBody @Valid EndCourseDTO dto){
        return selectionService.endCourse(UserContext.getUserId(), dto);
    }

    /**
     * 查询已选课程
     * @param dto 查询参数，支持null和仅传部分参数
     * @return 课程选择列表
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/selectionList")
    public Result<IPage<SelectionVO>> showSelectionList(@Valid SelectionQueryDTO dto) {
        dto.setDefaultValue();
        return selectionService.showSelectedList(UserContext.getUserId(), dto);
    }

    /**
     * 教师查询某课程的学生列表
     * @param cid 课程ID
     */
    @RequireRole(RoleEnum.TEACHER)
    @PostMapping("/courseStudents/{cid}")
    public Result<List<CourseStudentVO>> showCourseStudents(@PathVariable Long cid) {
        return selectionService.showCourseStudents(UserContext.getUserId(), cid);
    }
}
