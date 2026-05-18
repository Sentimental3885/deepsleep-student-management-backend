package com.deepsleep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.NoticeDTO;
import com.deepsleep.data.vo.NoticeVO;

public interface NoticeService {
    void publish(NoticeDTO dto);
    void delete(Long id);
    void update(Long id, NoticeDTO dto);
    Page<NoticeVO> list(int pageNum, int pageSize);
    NoticeVO getById(Long id);
}
