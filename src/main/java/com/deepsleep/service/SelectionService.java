package com.deepsleep.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.SelectionDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.SelectionVO;

public interface SelectionService {
    Result<IPage<CourseVO>> showAvailableList(Long sid, SelectionQueryDTO dto);

    Result<Void> pickCourse(Long sid, SelectionDTO dto);

    Result<Void> dropCourse(Long sid, SelectionDTO dto);

    Result<Void> endCourse(Long tid, EndCourseDTO dto);

    Result<IPage<SelectionVO>> showSelectedList(Long sid, SelectionQueryDTO dto);

    Long currentSize(Long cid);

}
