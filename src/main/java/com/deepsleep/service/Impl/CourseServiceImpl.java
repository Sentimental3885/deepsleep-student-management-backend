package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.AddCourseDTO;
import com.deepsleep.data.dto.UpdateCourseDTO;
import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseClazz;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.*;
import com.deepsleep.service.CourseService;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseClazzMapper ccMapper;

    @Resource
    private SelectionService selectionService;

    @Resource
    private ClazzMapper clazzMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CourseSelectionMapper selectionMapper;

    //添加课程
    @Transactional
    @Override
    public Result<Void> addCourse(AddCourseDTO dto) {
        if (teacherMapper.selectById(dto.getTid()) == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }
        for (Long zid: dto.getZids()) {
            if (clazzMapper.selectById(zid) == null) {
                throw new BusinessException(ResultCode.CLAZZ_NOT_FOUND);
            }
        }
        Course course = new Course(
              null, dto.getName(), dto.getTid(), dto.getCapacity(), dto.getCode(), dto.getSemester(),
              dto.getCredit(), CourseStatus.fromValue(dto.getStatus()), dto.getIntroduction(),
              LocalDateTime.now(), LocalDateTime.now()
        );
        try {
            courseMapper.insert(course);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ResultCode.COURSE_ALREADY_EXIST);
        }
        for (Long zid: dto.getZids()) {
            ccMapper.insert(new CourseClazz(
                    null, course.getId(), zid, LocalDateTime.now()
            ));
        }
        return Result.success();
    }

    //课程详情
    @Override
    public Result<CourseVO> getDetail(Long cid) {
        Course po = courseMapper.selectById(cid);
        if (po == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(po, vo);
        vo.setStatus(po.getStatus().getValue());
        User teacher = userMapper.selectById(po.getTeacherId());
        vo.setTeacherName(teacher.getName());
        vo.setSize(selectionService.currentSize(po.getId()));
        return Result.success(vo);
    }

    //删除课程
    @Override
    public Result<Void> deleteCourse(Long cid) {
        if (courseMapper.selectById(cid) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        courseMapper.deleteById(cid);
        ccMapper.delete(new LambdaQueryWrapper<CourseClazz>().eq(CourseClazz::getCourseId,cid));
        selectionMapper.delete(new LambdaQueryWrapper<CourseSelection>().eq(CourseSelection::getCourseId,cid));
        return Result.success();
    }

    //修改课程
    @Override
    public Result<Void> updateCourse(Long cid, UpdateCourseDTO dto) {
        Course course = courseMapper.selectById(cid);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        if (selectionService.currentSize(cid) > dto.getCapacity()) {
            throw new BusinessException(ResultCode.CAPACITY_NOT_ENOUGH);
        }
        course.setCapacity(dto.getCapacity());
        course.setStatus(CourseStatus.fromValue(dto.getStatus()));
        course.setIntroduction(dto.getIntroduction());
        courseMapper.updateById(course);
        return Result.success();
    }

    //验证教师是否为授课教师
    @Override
    public void verifyTeacher(Long tid, Long cid) {
        if (!courseMapper.selectById(cid).getTeacherId().equals(tid)) {
            throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
        }
    }

}
