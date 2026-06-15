package com.deepsleep.controller;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.ClassroomScheduleQueryDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @RequireRole({RoleEnum.ADMIN, RoleEnum.TEACHER})
    @GetMapping("/classroom/{classroomId}")
    public Result<List<ScheduleVO>> getClassroomSchedule(
            @PathVariable Long classroomId,
            @Validated ClassroomScheduleQueryDTO dto
    ) {
        return Result.success(scheduleService.getClassroomSchedule(classroomId, dto));
    }
}
//