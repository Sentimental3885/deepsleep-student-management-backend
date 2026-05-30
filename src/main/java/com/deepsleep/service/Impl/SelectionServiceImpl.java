package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.SelectionDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.*;
import com.deepsleep.data.vo.CourseStudentVO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.SelectionVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.*;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SelectionServiceImpl implements SelectionService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CourseSelectionMapper selectionMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private UserMapper userMapper;

    //根据课序号查询现有人数
    @Override
    public Long currentSize(Long cid) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid);
        return selectionMapper.selectCount(wrapper);
    }

    @Override
    public Result<List<CourseStudentVO>> showCourseStudents(Long tid, Long cid) {
        Course course = courseMapper.selectById(cid);
        if (course == null) {
            return Result.error(ResultCode.COURSE_NOT_FOUND);
        }

        verifyTeacher(cid,tid);

        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid)
                .ne(CourseSelection::getStatus, SelectionStatus.DROPPED);

        List<CourseSelection> selections = selectionMapper.selectList(wrapper);
        List<CourseStudentVO> list = selections.stream().map(selection -> {
            CourseStudentVO vo = new CourseStudentVO();
            User student = userMapper.selectById(selection.getStudentId());
            vo.setStudentId(selection.getStudentId());
            vo.setStudentName(student.getName());
            vo.setUsername(student.getUsername());
            vo.setScore(selection.getScore());
            vo.setSelectionStatus(selection.getStatus());
            return vo;
        }).toList();

        return Result.success(list);
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

    //获取对应班级的课程列表
    @Override
    public Result<IPage<CourseVO>> showAvailableList(Long sid, SelectionQueryDTO dto) {
        Student student = studentMapper.selectById(sid);
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, CourseStatus.ON)
                .exists("SELECT 1 FROM course_clazz cc WHERE cc.course_id = course.id " +
                        "AND cc.clazz_id = {0}", student.getClazzId())
                //排除已选/已结业课程
                .notExists("SELECT 1 FROM course_selection cs WHERE cs.student_id = {0} " +
                        "AND course.id = cs.course_id AND cs.status != {1}",
                        sid, SelectionStatus.DROPPED);
        Page<Course> page = new Page<>(dto.getCurrent(), dto.getSize());
        return Result.success(
                courseMapper.selectPage(page, wrapper).convert(po -> {
                    CourseVO vo = new CourseVO();
                    //拷贝同名字段
                    BeanUtils.copyProperties(po, vo);
                    vo.setStatus(po.getStatus().getValue());
                    User teacher = userMapper.selectById(po.getTeacherId());
                    vo.setTeacherName(teacher.getName());
                    vo.setSize(currentSize(po.getId()));
                    return vo;})
        );
    }

    //选课
    @Override
    public Result<Void> pickCourse(Long sid, SelectionDTO dto) {
        verifyStudent(sid);
        verifyCourse(dto.getCid());
        CourseSelection selection = getSelection(sid, dto.getCid());
        if (selection == null) {
            selectionMapper.insert(new CourseSelection(
                    null, sid, dto.getCid(), null, SelectionStatus.PICKED,
                    LocalDateTime.now(), LocalDateTime.now()
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
    public Result<Void> dropCourse(Long sid, SelectionDTO dto) {
        //不用单独验证Student和Course了，会被SELECTION_NOT_FOUND一并验证
        CourseSelection selection = getSelection(sid, dto.getCid());
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else {
            if (selection.getStatus() == SelectionStatus.DROPPED) {
                return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
            } else if(selection.getStatus() == SelectionStatus.OVER){
                return Result.error(ResultCode.COURSE_ALREADY_OVER);
            } else {
                selection.setStatus(SelectionStatus.DROPPED);
                selection.setUpdateTime(LocalDateTime.now());
                selectionMapper.updateById(selection);
                return Result.success();
            }
        }
    }

    //结课，或修改成绩
    @Override
    public Result<Void> endCourse(Long tid, EndCourseDTO dto) {
        verifyCourse(dto.getCid());
        verifyTeacher(dto.getCid(), tid);
        CourseSelection selection = getSelection(dto.getSid(), dto.getCid());
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else if(selection.getStatus() == SelectionStatus.DROPPED) {
            return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
        } else {
            double score = dto.getScore();
            if (score > 100 || score < 0) {
                return Result.error(ResultCode.INVALID_SCORE);
            }
            //对分数根据指定精度四舍五入
            double precision = 1e-2;
            double roundedScore = (double)Math.round(score/precision) * precision;
            if (roundedScore != score) {
                return Result.error(ResultCode.INVALID_SCORE);
            }
            selection.setStatus(SelectionStatus.OVER);
            selection.setScore(score);
            selection.setUpdateTime(LocalDateTime.now());
            selectionMapper.updateById(selection);
            return Result.success();
        }
    }

    //确认选课列表
    @Override
    public Result<IPage<SelectionVO>> showSelectedList(Long sid, SelectionQueryDTO dto) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid);
        Page<CourseSelection> page = new Page<>(dto.getCurrent(), dto.getSize());
        return Result.success(
                selectionMapper.selectPage(page, wrapper).convert(selection -> {
                    SelectionVO vo = new SelectionVO();
                    Course course = courseMapper.selectById(selection.getCourseId());
                    BeanUtils.copyProperties(course, vo);
                    User teacher = userMapper.selectById(course.getTeacherId());
                    vo.setCourseStatus(course.getStatus().getValue());
                    vo.setTeacherName(teacher.getName());
                    vo.setSize(currentSize(course.getId()));
                    vo.setSelectionStatus(selection.getStatus().getValue());
                    vo.setScore(selection.getScore());
                    return vo;
                })
        );
    }
}
