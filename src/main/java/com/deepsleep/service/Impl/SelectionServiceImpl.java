package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.vo.Result;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.CourseMapper;
import com.deepsleep.mapper.CourseSelectionMapper;
import com.deepsleep.mapper.StudentMapper;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectionServiceImpl implements SelectionService {

    @Resource
    private CourseSelectionMapper selectionMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private StudentMapper studentMapper;

    //根据课序号查询现有人数
    @Override
    public Long currentSize(Long cid) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid);
        return selectionMapper.selectCount(wrapper);
    }

    //验证课程状态
    private void verifyCourse(Long cid) {
        Course course = courseMapper.selectById(cid);
        if (course == null){
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        } else if (course.getStatus() == CourseStatus.OFF) {
            throw new BusinessException(ResultCode.COURSE_UNPICKABLE);
        } else if (currentSize(cid) >= course.getCapacity()) {
            throw new BusinessException(ResultCode.COURSE_FULL);
        }
    }

    //获取选课条目
    private CourseSelection getSelection(Long sid, Long cid){
        //筛选已有条目，UNIQUE_KEY决定表里最多只有一个条目，所以用selectOne
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid).eq(CourseSelection::getStudentId, sid);
        return selectionMapper.selectOne(wrapper);
    }

    //验证学生
    private void verifyStudent(Long sid) {
        if (studentMapper.selectById(sid) == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }
    }

    //验证教师
    private void verifyTeacher(Long cid, Long tid) {
        if (!courseMapper.selectById(cid).getTeacherId().equals(tid)) {
            throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
        }
    }

    //选课
    @Override
    public Result<Void> pickCourse(Long sid, Long cid) {
        verifyStudent(sid);
        verifyCourse(cid);
        CourseSelection selection = getSelection(sid, cid);
        if (selection == null) {
            selectionMapper.insert(new CourseSelection(
                    sid, cid, null, SelectionStatus.PICKED
            ));
            return Result.success();
        } else {
            if (selection.getStatus() == SelectionStatus.PICKED) {
                return Result.error(ResultCode.COURSE_ALREADY_PICKED);
            } else if (selection.getStatus() == SelectionStatus.OVER) {
                return Result.error(ResultCode.COURSE_ALREADY_OVER);
            } else {
                selection.setStatus(SelectionStatus.PICKED);
                selectionMapper.updateById(selection);
                return Result.success();
            }
        }
    }

    //退课
    @Override
    public Result<Void> dropCourse(Long sid, Long cid) {
        //似乎不用单独verify Student和Course了
        CourseSelection selection = getSelection(sid, cid);
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else {
            if (selection.getStatus() == SelectionStatus.DROPPED) {
                return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
            } else if(selection.getStatus() == SelectionStatus.OVER){
                return Result.error(ResultCode.COURSE_ALREADY_OVER);
            } else {
                selection.setStatus(SelectionStatus.DROPPED);
                selectionMapper.updateById(selection);
                return Result.success();
            }
        }
    }

    //结课，或修改成绩
    @Override
    public Result<Void> endCourse(Long sid, Long cid, Double score, Long tid) {
        verifyCourse(cid);
        verifyTeacher(cid, tid);
        CourseSelection selection = getSelection(sid, cid);
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else if(selection.getStatus() == SelectionStatus.DROPPED) {
            return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
        } else if(score<0 || score>100){
            return Result.error(ResultCode.INVALID_SCORE);
        } else {
            selection.setStatus(SelectionStatus.OVER);
            selection.setScore(score);
            selectionMapper.updateById(selection);
            return Result.success();
        }
    }

    //确认选课列表（使用MybatisPlus内置分页）
    @Override
    public Result<Page<CourseSelection>> showList(Long sid, long current, long size, List<SelectionStatus> statuses) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid);
        wrapper.in(CourseSelection::getStatus, statuses);
        Page<CourseSelection> page = new Page<>(current, size);
        selectionMapper.selectPage(page, wrapper);
        return Result.success(page);
    }

    //无筛选条件重载
    @Override
    public Result<Page<CourseSelection>> showList(Long sid, long current, long size) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid);
        Page<CourseSelection> page = new Page<>(current, size);
        selectionMapper.selectPage(page, wrapper);
        return Result.success(page);
    }
}
