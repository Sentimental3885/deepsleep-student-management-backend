package com.deepsleep.controller;

import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.ClazzDTO;
import com.deepsleep.data.dto.DeptDTO;
import com.deepsleep.data.dto.MajorDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.ClazzVO;
import com.deepsleep.data.vo.DeptVO;
import com.deepsleep.data.vo.MajorVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    /* 学院管理 */
    @RequireLogin
    @GetMapping("/dept/list")
    public Result<List<DeptVO>> listDepts() {
        return Result.success(organizationService.listDepts());
    }

    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/dept")
    public Result<Void> addDept(@RequestBody @Valid DeptDTO dto) {
        organizationService.addDept(dto);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/dept/{id}")
    public Result<Void> updateDept(@PathVariable Long id,
                                   @RequestBody @Valid DeptDTO dto) {
        organizationService.updateDept(id, dto);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/dept/{id}")
    public Result<Void> deleteDept(@PathVariable Long id) {
        organizationService.deleteDept(id);
        return Result.success();
    }

    /* 专业管理 */
    @RequireLogin
    @GetMapping("/major/list")
    public Result<List<MajorVO>> listMajors(@RequestParam(required = false) Long deptId) {
        return Result.success(organizationService.listMajors(deptId));
    }

    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/major")
    public Result<Void> addMajor(@RequestBody @Valid MajorDTO dto) {
        organizationService.addMajor(dto);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/major/{id}")
    public Result<Void> updateMajor(@PathVariable Long id,
                                    @RequestBody @Valid MajorDTO dto) {
        organizationService.updateMajor(id, dto);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/major/{id}")
    public Result<Void> deleteMajor(@PathVariable Long id) {
        organizationService.deleteMajor(id);
        return Result.success();
    }

    /* 班级管理 */
    @RequireLogin
    @GetMapping("/clazz/list")
    public Result<List<ClazzVO>> listClazzes(@RequestParam(required = false) Long majorId) {
        return Result.success(organizationService.listClazzes(majorId));
    }

    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/clazz")
    public Result<Void> addClazz(@RequestBody @Valid ClazzDTO dto) {
        organizationService.addClazz(dto);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/clazz/{id}")
    public Result<Void> updateClazz(@PathVariable Long id,
                                    @RequestBody @Valid ClazzDTO dto) {
        organizationService.updateClazz(id, dto);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/clazz/{id}")
    public Result<Void> deleteClazz(@PathVariable Long id) {
        organizationService.deleteClazz(id);
        return Result.success();
    }
}