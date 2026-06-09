package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.NoticeDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.Notice;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.NoticeVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.NoticeMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;
    private final UserMapper userMapper;
    private final FileStorage fileStorage;

    @Override
    public void publish(NoticeDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPublisherId(UserContext.getUserId());
        noticeMapper.insert(notice);
    }

    @Override
    public void delete(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        noticeMapper.deleteById(id);
    }

    @Override
    public void update(Long id, NoticeDTO dto) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        noticeMapper.updateById(notice);
    }

    @Override
    public Page<NoticeVO> list(int pageNum, int pageSize) {
        Page<Notice> page = noticeMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Notice>()
                        .orderByDesc(Notice::getCreateTime)
        );

        // 转换成 VO
        Page<NoticeVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<NoticeVO> voList = page.getRecords().stream().map(notice -> {
            NoticeVO vo = new NoticeVO();
            vo.setId(notice.getId());
            vo.setTitle(notice.getTitle());
            vo.setContent(notice.getContent());
            vo.setPublisherId(notice.getPublisherId());
            vo.setCreateTime(notice.getCreateTime());
            vo.setUpdateTime(notice.getUpdateTime());

            // 查发布人姓名
            User publisher = userMapper.selectById(notice.getPublisherId());
            if (publisher != null) {
                vo.setPublisherName(publisher.getName());
                vo.setPublisherAvatar(fileStorage.getUrl(publisher.getAvatar()));
            }
            return vo;
        }).toList();

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public NoticeVO getById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setPublisherId(notice.getPublisherId());
        vo.setCreateTime(notice.getCreateTime());
        vo.setUpdateTime(notice.getUpdateTime());
        User publisher = userMapper.selectById(notice.getPublisherId());
        if (publisher != null) {
            vo.setPublisherName(publisher.getName());
            vo.setPublisherAvatar(fileStorage.getUrl(publisher.getAvatar()));
        }
        return vo;
    }
}
