package com.deepsleep.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.annotation.RequireLogin;
import com.deepsleep.annotation.RequireRole;
import com.deepsleep.data.dto.NoticeDTO;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.vo.NoticeVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 发布教务公告
     */
    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/admin/notice")
    public Result<Void> publish(@RequestBody @Valid NoticeDTO dto) {
        noticeService.publish(dto);
        return Result.success();
    }

    /**
     * 删除公告
     */
    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/admin/notice/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }

    /**
     * 修改公告
     * @param dto 标题+内容
     */
    @RequireRole(RoleEnum.ADMIN)
    @PutMapping("/admin/notice/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @RequestBody @Valid NoticeDTO dto) {
        noticeService.update(id, dto);
        return Result.success();
    }

    /**
     * 查询公告列表
     * @param pageNum 页数
     * @param pageSize 分页大小
     */
    @RequireLogin
    @GetMapping("/notice/list")
    public Result<Page<NoticeVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(noticeService.list(pageNum, pageSize));
    }

    /**
     * 查询公告详情
     */
    @RequireLogin
    @GetMapping("/notice/{id}")
    public Result<NoticeVO> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }
}