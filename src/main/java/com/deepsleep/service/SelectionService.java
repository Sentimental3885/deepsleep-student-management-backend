package com.deepsleep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.vo.Result;

import java.util.List;

public interface SelectionService {
    Result<Void> pickCourse(Long sid, Long cid);

    Result<Void> dropCourse(Long sid, Long cid);

    Result<Void> endCourse(Long sid, Long cid, Double score, Long tid);

    Result<Page<CourseSelection>> showList(Long sid, long current, long size, List<SelectionStatus> statuses);

    Result<Page<CourseSelection>> showList(Long sid, long current, long size);

    Long currentSize(Long cid);

}
