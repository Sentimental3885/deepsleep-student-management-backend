package com.deepsleep.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.SelectionDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.SelectionVO;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/selection")
public class SelectionController {

    @Resource
    private SelectionService selectionService;

    /**
     * 获取可选课程列表
     * @param dto 查询参数
     * @return 分页课程列表
     */
    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/courseList")
    public Result<IPage<CourseVO>> showCourseList(@RequestBody @Valid SelectionQueryDTO dto) {
        return selectionService.showAvailableList(UserContext.getUserId(), dto);
    }

    /**
     * 选课
     * @param dto 仅含课程id
     */
    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/pick")
    public Result<Void> pickCourse(@RequestBody @Valid SelectionDTO dto){
        return selectionService.pickCourse(UserContext.getUserId(), dto);
    }

    /**
     * 退课
     * @param dto 仅含课程id
     */
    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/drop")
    public Result<Void> dropCourse(@RequestBody @Valid SelectionDTO dto){
        return selectionService.dropCourse(UserContext.getUserId(), dto);
    }

    /**
     * 结课
     * @param dto 结课信息
     */
    @RequireRole(RoleEnum.TEACHER)
    @PostMapping("/end")
    public Result<Void> endCourse(@RequestBody @Valid EndCourseDTO dto){
        return selectionService.endCourse(UserContext.getUserId(), dto);
    }

    /**
     * 查询已选课程
     * @param dto 查询参数
     * @return 课程选择列表
     */
    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/selectionList")
    public Result<IPage<SelectionVO>> showSelectionList(@RequestBody @Valid SelectionQueryDTO dto) {
        return selectionService.showSelectedList(UserContext.getUserId(), dto);
    }
}
