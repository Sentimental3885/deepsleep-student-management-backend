package com.deepsleep.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.vo.Result;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.CourseSelectionMapper;
import com.deepsleep.mapper.StudentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectionService {

    @Resource
    private CourseSelectionMapper selectionMapper;

    @Resource
    private StudentMapper studentMapper;

    /*
        等Course写出来还要验证:
        COURSE_NOT_FOUND(2001, "课程不存在", HttpStatus.NOT_FOUND),
        COURSE_UNPICKABLE(2002, "课程不可选", HttpStatus.CONFLICT),
        COURSE_FULL(2003, "课程已满员", HttpStatus.CONFLICT),
        -------------------------------------------------------------
        @Resource
        private CourseMapper courseMapper;

        private void verifyCourse(Long cid);
    */

    //验证选课条目
    private CourseSelection verifySelection(Long sid, Long cid){
        //筛选已有条目
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid).eq(CourseSelection::getStudentId, sid);
        return selectionMapper.selectOne(wrapper);
        //本来用selectList写，发现表有个UNIQUE_KEY，最多就一个条目，也不用验证了
    }
    //验证学生
    private void verifyStudent(Long sid) {
        if (studentMapper.selectById(sid) == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }
    }
    //选课
    public Result<Void> pickCourse(Long sid, Long cid) {
        verifyStudent(sid);
        //verifyCourse(cid);
        CourseSelection selection = verifySelection(sid, cid);
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
    public Result<Void> dropCourse(Long sid, Long cid) {
        //似乎不用单独verify Student和Course了
        CourseSelection selection = verifySelection(sid, cid);
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
    //结课
    public Result<Void> endCourse(Long sid, Long cid, Double score) {
        CourseSelection selection = verifySelection(sid, cid);
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else if(selection.getStatus() == SelectionStatus.DROPPED) {
            return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
        } else if(selection.getStatus() == SelectionStatus.OVER){
            return Result.error(ResultCode.COURSE_ALREADY_OVER);
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
    public Result<Page<CourseSelection>> showList(Long sid, long current, long size, List<SelectionStatus> statuses) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid);
        wrapper.in(CourseSelection::getStatus, statuses);
        Page<CourseSelection> page = new Page<>(current, size);
        selectionMapper.selectPage(page, wrapper);
        return Result.success(page);
    }
    //无筛选条件重载
    public Result<Page<CourseSelection>> showList(Long sid, long current, long size) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid);
        Page<CourseSelection> page = new Page<>(current, size);
        selectionMapper.selectPage(page, wrapper);
        return Result.success(page);
    }
}
