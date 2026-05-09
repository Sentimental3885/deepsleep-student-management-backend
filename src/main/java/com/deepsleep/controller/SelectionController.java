package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.SelectionDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
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

    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/pick")
    public Result<Void> pickCourse(@RequestBody @Valid SelectionDTO dto){
        return selectionService.pickCourse(UserContext.getUserId(), dto.getCid());
    }

    @RequireRole(RoleEnum.STUDENT)
    @PostMapping("/drop")
    public Result<Void> dropCourse(@RequestBody @Valid SelectionDTO dto){
        return selectionService.dropCourse(UserContext.getUserId(), dto.getCid());
    }

    @RequireRole(RoleEnum.TEACHER)
    @PostMapping("/end")
    public Result<Void> endCourse(@RequestBody @Valid EndCourseDTO dto){
        return selectionService.endCourse(dto.getSid(), dto.getCid(), dto.getScore(), UserContext.getUserId());
    }

}
