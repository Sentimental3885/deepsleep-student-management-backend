package com.deepsleep.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.CourseStudentQueryDTO;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.EndCourseBatchDTO;
import com.deepsleep.data.dto.ScoreQueryDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.CourseStudentVO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScoreVO;
import com.deepsleep.data.vo.SelectionCheckVO;
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
     * 批量结课/录入成绩
     */
    @RequireRole(RoleEnum.TEACHER)
    @PutMapping("/end/batch")
    public Result<Void> endCourseBatch(@RequestBody @Valid EndCourseBatchDTO dto) {
        return selectionService.endCourseBatch(UserContext.getUserId(), dto);
    }

    /**
     * 选课前可选性检查
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/course/{cid}/check")
    public Result<SelectionCheckVO> checkSelectable(@PathVariable Long cid) {
        return selectionService.checkSelectable(UserContext.getUserId(), cid);
    }

    /**
     * 查询已选课程
     * @param dto 查询参数，支持null和仅传部分参数
     * @return 课程选择列表
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/selectionList")
    public Result<IPage<CourseVO>> showSelectionList(@Valid SelectionQueryDTO dto) {
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

    /**
     * 查询某课程学生列表
     */
    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @GetMapping("/courseStudents/{cid}")
    public Result<IPage<CourseStudentVO>> showCourseStudents(@PathVariable Long cid,
                                                             @Valid CourseStudentQueryDTO dto) {
        dto.setDefaultValue();
        return selectionService.showCourseStudents(UserContext.getUserId(), UserContext.getRole(), cid, dto);
    }

    /**
     * 学生查询成绩
     * @param dto 学期+分页参数
     * @return 分页的成绩单
     */
    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/score/list")
    public Result<IPage<ScoreVO>> showScoreList(@Valid ScoreQueryDTO dto) {
        dto.setDefaultValue();
        return selectionService.showScoreList(UserContext.getUserId(), dto);
    }

    @RequireRole(RoleEnum.STUDENT)
    @GetMapping("/score/{cid}")
    public Result<ScoreVO> showScore(@PathVariable Long cid) {
        return selectionService.getScoreDetail(UserContext.getUserId(), cid);
    }
}
