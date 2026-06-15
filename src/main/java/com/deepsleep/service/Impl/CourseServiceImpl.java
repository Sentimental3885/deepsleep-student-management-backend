package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.AddCourseDTO;
import com.deepsleep.data.dto.CourseClazzUpdateDTO;
import com.deepsleep.data.dto.CourseQueryDTO;
import com.deepsleep.data.dto.ScheduleDTO;
import com.deepsleep.data.dto.UpdateCourseDTO;
import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.*;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.ClazzVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.*;
import com.deepsleep.service.CourseService;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private DeptMapper deptMapper;

    @Resource
    private MajorMapper majorMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FileStorage fileStorage;

    @Resource
    private CourseSelectionMapper selectionMapper;

    @Resource
    private CourseScheduleMapper scheduleMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    private CourseVO toCourseVO(Course po) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(po, vo);
        vo.setStatus(po.getStatus().getValue());
        User teacher = userMapper.selectById(po.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getName());
            vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
        }
        vo.setSize(selectionService.currentSize(po.getId()));
        vo.setClazzes(getClazzesByCourse(po.getId()));
        return vo;
    }

    private List<ClazzVO> getClazzesByCourse(Long cid) {
        List<CourseClazz> relations = ccMapper.selectList(
                new LambdaQueryWrapper<CourseClazz>().eq(CourseClazz::getCourseId, cid)
        );
        return relations.stream().map(relation -> {
            Clazz clazz = clazzMapper.selectById(relation.getClazzId());
            if (clazz == null) return null;
            ClazzVO vo = new ClazzVO();
            vo.setId(clazz.getId());
            vo.setName(clazz.getName());
            vo.setDeptId(clazz.getDeptId());
            vo.setMajorId(clazz.getMajorId());
            vo.setGrade(clazz.getGrade());
            Dept dept = deptMapper.selectById(clazz.getDeptId());
            if (dept != null) vo.setDeptName(dept.getName());
            Major major = majorMapper.selectById(clazz.getMajorId());
            if (major != null) vo.setMajorName(major.getName());
            return vo;
        }).filter(Objects::nonNull).toList();
    }

    //添加课程，暂不加排课
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
        return Result.success(toCourseVO(po));
    }

    @Override
    public Result<IPage<CourseVO>> listCourses(CourseQueryDTO dto) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), w -> w
                        .like(Course::getName, dto.getKeyword())
                        .or()
                        .like(Course::getCode, dto.getKeyword()))
                .eq(dto.getSemester() != null && !dto.getSemester().isBlank(), Course::getSemester, dto.getSemester())
                .eq(dto.getStatus() != null, Course::getStatus,
                        dto.getStatus() == null ? null : CourseStatus.fromValue(dto.getStatus()))
                .eq(dto.getTeacherId() != null, Course::getTeacherId, dto.getTeacherId())
                .exists(dto.getClazzId() != null,
                        "SELECT 1 FROM course_clazz cc WHERE cc.course_id = course.id AND cc.clazz_id = {0}",
                        dto.getClazzId())
                .orderByDesc(Course::getCreateTime);
        Page<Course> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return Result.success(courseMapper.selectPage(page, wrapper).convert(this::toCourseVO));
    }

    @Override
    public Result<List<ClazzVO>> getCourseClazzes(Long cid) {
        if (courseMapper.selectById(cid) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        return Result.success(getClazzesByCourse(cid));
    }

    @Transactional
    @Override
    public Result<Void> updateCourseClazzes(Long cid, CourseClazzUpdateDTO dto) {
        if (courseMapper.selectById(cid) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        List<Long> clazzIds = dto.getClazzIds().stream().distinct().toList();
        for (Long clazzId : clazzIds) {
            if (clazzMapper.selectById(clazzId) == null) {
                throw new BusinessException(ResultCode.CLAZZ_NOT_FOUND);
            }
        }
        ccMapper.delete(new LambdaQueryWrapper<CourseClazz>().eq(CourseClazz::getCourseId, cid));
        for (Long clazzId : clazzIds) {
            ccMapper.insert(new CourseClazz(null, cid, clazzId, LocalDateTime.now()));
        }
        return Result.success();
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
        scheduleMapper.delete(new LambdaQueryWrapper<CourseSchedule>().eq(CourseSchedule::getCourseId,cid));
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

    @Override
    public Result<List<ScheduleVO>> getScheduleByCourse(Long cid) {
        if (courseMapper.selectById(cid) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        List<CourseSchedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>().eq(CourseSchedule::getCourseId,cid)
        );
        return Result.success(schedules.stream().map(schedule -> {
            ScheduleVO vo = new ScheduleVO();
            Course course = courseMapper.selectById(schedule.getCourseId());
            Classroom classroom = classroomMapper.selectById(schedule.getClassroomId());
            User teacher = userMapper.selectById(course.getTeacherId());
            vo.setId(schedule.getId());
            vo.setCourseId(course.getId());
            vo.setCourseName(course.getName());
            vo.setTeacherName(teacher.getName());
            vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
            vo.setWeekday(schedule.getWeekday());
            vo.setSection(schedule.getSection());
            vo.setStartWeek(schedule.getStartWeek());
            vo.setEndWeek(schedule.getEndWeek());
            vo.setClassroomId(classroom.getId());
            vo.setClassroomName(classroom.getName());
            return vo;
        }).toList());
    }

    private void verifyClassroom(Long rid) {
        if (classroomMapper.selectById(rid) == null) {
            throw new BusinessException(ResultCode.CLASSROOM_NOT_FOUND);
        }
    }

    //增加排课
    @Override
    public Result<Void> addSchedule(Long cid, ScheduleDTO dto) {
        //检查新排课是否与表中同时刻同教室的课冲突
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getWeekday, dto.getWeekday()).eq(CourseSchedule::getSection, dto.getSection())
                .ge(CourseSchedule::getEndWeek, dto.getStartWeek()).le(CourseSchedule::getStartWeek, dto.getEndWeek())
                .eq(CourseSchedule::getClassroomId, dto.getRid());
        if (scheduleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.SCHEDULE_CONFLICT);
        }
        verifyClassroom(dto.getRid());
        CourseSchedule schedule = new CourseSchedule(
                null, cid, dto.getWeekday(), dto.getSection(),
                dto.getStartWeek(), dto.getEndWeek(), dto.getRid(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        scheduleMapper.insert(schedule);
        return Result.success();
    }

    //删除排课
    @Override
    public Result<Void> deleteSchedule(Long cid, Long scid) {
        CourseSchedule schedule = scheduleMapper.selectById(scid);
        if (schedule == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        } else if (!Objects.equals(schedule.getCourseId(), cid)) {
            throw new BusinessException(ResultCode.SCHEDULE_COURSE_MISMATCH);
        }
        scheduleMapper.deleteById(scid);
        return Result.success();
    }

    //更改排课
    @Override
    public Result<Void> updateSchedule(Long cid, Long scid, ScheduleDTO dto) {
        CourseSchedule schedule = scheduleMapper.selectById(scid);
        if (schedule == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        } else if (!Objects.equals(schedule.getCourseId(), cid)) {
            throw new BusinessException(ResultCode.SCHEDULE_COURSE_MISMATCH);
        }
        verifyClassroom(dto.getRid());
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getWeekday, dto.getWeekday()).eq(CourseSchedule::getSection, dto.getSection())
                .ge(CourseSchedule::getEndWeek, dto.getStartWeek()).le(CourseSchedule::getStartWeek, dto.getEndWeek())
                .eq(CourseSchedule::getClassroomId, dto.getRid()).ne(CourseSchedule::getId, scid);
        if (scheduleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.SCHEDULE_CONFLICT);
        }
        BeanUtils.copyProperties(dto, schedule);
        schedule.setClassroomId(dto.getRid());
        schedule.setUpdateTime(LocalDateTime.now());
        scheduleMapper.updateById(schedule);
        return Result.success();
    }

    //验证教师是否为授课教师
    @Override
    public void verifyTeacher(Long tid, Long cid) {
        if (courseMapper.selectById(cid) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        if (!courseMapper.selectById(cid).getTeacherId().equals(tid)) {
            throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
        }
    }

}
