package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.CourseStudentQueryDTO;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.EndCourseBatchDTO;
import com.deepsleep.data.dto.ScoreDetailDTO;
import com.deepsleep.data.dto.ScoreQueryDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.po.CourseSchedule;
import com.deepsleep.data.po.Classroom;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.CourseStudentVO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.ScoreVO;
import com.deepsleep.data.vo.SelectionCheckVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.*;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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

    @Resource
    private FileStorage fileStorage;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private CourseScheduleMapper scheduleMapper;

    @Resource
    private ClassroomMapper classroomMapper;

    @Override
    public Long currentSize(Long cid) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid)
                .eq(CourseSelection::getStatus, SelectionStatus.PICKED);
        return selectionMapper.selectCount(wrapper);
    }

    @Override
    public Result<List<CourseStudentVO>> showCourseStudents(Long tid, Long cid) {
        verifyCourse(cid, true, true);
        verifyTeacher(cid, tid);

        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid)
                .ne(CourseSelection::getStatus, SelectionStatus.DROPPED);

        List<CourseSelection> selections = selectionMapper.selectList(wrapper);
        List<CourseStudentVO> list = selections.stream().map(selection -> {
            CourseStudentVO vo = new CourseStudentVO();
            User student = userMapper.selectById(selection.getStudentId());
            vo.setStudentId(selection.getStudentId());
            vo.setStudentName(student.getName());
            vo.setStudentAvatar(fileStorage.getUrl(student.getAvatar()));
            vo.setUsername(student.getUsername());
            vo.setScore(selection.getScore());
            vo.setSelectionStatus(selection.getStatus());
            return vo;
        }).toList();

        return Result.success(list);
    }

    @Override
    public Result<IPage<CourseStudentVO>> showCourseStudents(Long operatorId, Integer role, Long cid, CourseStudentQueryDTO dto) {
        verifyCourse(cid, true, true);
        if (role == RoleEnum.TEACHER.getCode()) {
            verifyTeacher(cid, operatorId);
        }

        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid);
        if (dto.getStatus() == null) {
            wrapper.ne(CourseSelection::getStatus, SelectionStatus.DROPPED);
        } else {
            wrapper.eq(CourseSelection::getStatus, selectionStatusFromValue(dto.getStatus()));
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) {
            String keyword = "%" + dto.getKeyword() + "%";
            wrapper.exists("SELECT 1 FROM user u WHERE u.id = course_selection.student_id " +
                    "AND (u.name LIKE {0} OR u.username LIKE {0})", keyword);
        }
        wrapper.orderByAsc(CourseSelection::getStudentId);
        Page<CourseSelection> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return Result.success(selectionMapper.selectPage(page, wrapper).convert(this::selectionToCourseStudentVO));
    }

    private SelectionStatus selectionStatusFromValue(Integer value) {
        return switch (value) {
            case 1 -> SelectionStatus.PICKED;
            case 2 -> SelectionStatus.DROPPED;
            case 3 -> SelectionStatus.OVER;
            default -> throw new BusinessException(ResultCode.BAD_REQUEST);
        };
    }

    private CourseStudentVO selectionToCourseStudentVO(CourseSelection selection) {
        CourseStudentVO vo = new CourseStudentVO();
        User student = userMapper.selectById(selection.getStudentId());
        vo.setStudentId(selection.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
            vo.setStudentAvatar(fileStorage.getUrl(student.getAvatar()));
            vo.setUsername(student.getUsername());
        }
        vo.setScore(selection.getScore());
        vo.setSelectionStatus(selection.getStatus());
        return vo;
    }

    private void verifyCourse(Long cid, boolean doAllowOff, boolean doAllowFull) {
        Course course = courseMapper.selectById(cid);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        } else if (!doAllowOff && course.getStatus() == CourseStatus.OFF) {
            throw new BusinessException(ResultCode.COURSE_UNPICKABLE);
        } else if (!doAllowFull && currentSize(cid) >= course.getCapacity()) {
            throw new BusinessException(ResultCode.COURSE_FULL);
        }
    }

    private CourseSelection getSelection(Long sid, Long cid) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid).eq(CourseSelection::getStudentId, sid);
        return selectionMapper.selectOne(wrapper);
    }

    private void verifyStudent(Long sid) {
        if (studentMapper.selectById(sid) == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }
    }

    private void verifyTeacher(Long cid, Long tid) {
        if (teacherMapper.selectById(tid) == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }
        if (!courseMapper.selectById(cid).getTeacherId().equals(tid)) {
            throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
        }
    }

    @Override
    public Result<IPage<CourseVO>> showAvailableList(Long sid, SelectionQueryDTO dto) {
        Student student = studentMapper.selectById(sid);
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, CourseStatus.ON)
                .and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), w -> w
                        .like(Course::getName, dto.getKeyword())
                        .or()
                        .like(Course::getCode, dto.getKeyword()))
                .eq(dto.getSemester() != null && !dto.getSemester().isBlank(), Course::getSemester, dto.getSemester())
                .ge(dto.getCreditMin() != null, Course::getCredit, dto.getCreditMin())
                .le(dto.getCreditMax() != null, Course::getCredit, dto.getCreditMax())
                .exists("SELECT 1 FROM course_clazz cc WHERE cc.course_id = course.id " +
                        "AND cc.clazz_id = {0}", student.getClazzId())
                .exists(dto.getWeekday() != null,
                        "SELECT 1 FROM course_schedule sc WHERE sc.course_id = course.id AND sc.weekday = {0}",
                        dto.getWeekday())
                .notExists("SELECT 1 FROM course_selection cs WHERE cs.student_id = {0} " +
                                "AND course.id = cs.course_id AND cs.status != {1}",
                        sid, SelectionStatus.DROPPED)
                .orderByAsc(Course::getId);
        if (Boolean.TRUE.equals(dto.getNoConflictOnly())) {
            List<CourseVO> filtered = courseMapper.selectList(wrapper).stream()
                    .map(po -> selectionToAvailableCourseVO(sid, po))
                    .filter(vo -> Boolean.TRUE.equals(vo.getSelectable()))
                    .toList();
            Page<CourseVO> result = new Page<>(dto.getPageNum(), dto.getPageSize(), filtered.size());
            int fromIndex = Math.min((dto.getPageNum() - 1) * dto.getPageSize(), filtered.size());
            int toIndex = Math.min(fromIndex + dto.getPageSize(), filtered.size());
            result.setRecords(filtered.subList(fromIndex, toIndex));
            return Result.success(result);
        }
        Page<Course> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return Result.success(courseMapper.selectPage(page, wrapper).convert(po -> selectionToAvailableCourseVO(sid, po)));
    }

    @Override
    public Result<Void> pickCourse(Long sid, Long cid) {
        verifyStudent(sid);
        verifyCourse(cid, false, false);
        Course course = courseMapper.selectById(cid);
        CourseSelection selection = getSelection(sid, cid);
        if (selection == null) {
            verifyScheduleConflict(sid, course);
            selectionMapper.insert(new CourseSelection(
                    null, sid, cid, null, SelectionStatus.PICKED,
                    LocalDateTime.now(), LocalDateTime.now()
            ));
            return Result.success();
        } else {
            if (selection.getStatus() == SelectionStatus.PICKED) {
                return Result.error(ResultCode.COURSE_ALREADY_PICKED);
            } else if (selection.getStatus() == SelectionStatus.OVER) {
                return Result.error(ResultCode.COURSE_ALREADY_OVER);
            } else {
                verifyScheduleConflict(sid, course);
                selection.setStatus(SelectionStatus.PICKED);
                selectionMapper.updateById(selection);
                return Result.success();
            }
        }
    }

    private void verifyScheduleConflict(Long sid, Course targetCourse) {
        if (!findScheduleConflicts(sid, targetCourse).isEmpty()) {
            throw new BusinessException(ResultCode.COURSE_TIME_CONFLICT);
        }
    }

    private List<CourseSchedule> findScheduleConflicts(Long sid, Course targetCourse) {
        List<CourseSchedule> targetSchedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>().eq(CourseSchedule::getCourseId, targetCourse.getId())
        );
        if (targetSchedules.isEmpty()) return List.of();

        List<CourseSelection> selections = selectionMapper.selectList(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getStudentId, sid)
                        .eq(CourseSelection::getStatus, SelectionStatus.PICKED)
        );
        if (selections.isEmpty()) return List.of();

        List<Long> selectedCourseIds = selections.stream().map(CourseSelection::getCourseId).toList();
        List<Course> selectedCourses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .in(Course::getId, selectedCourseIds)
                        .eq(Course::getSemester, targetCourse.getSemester())
        );
        if (selectedCourses.isEmpty()) return List.of();

        List<Long> sameSemesterCourseIds = selectedCourses.stream().map(Course::getId).toList();
        List<CourseSchedule> selectedSchedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>().in(CourseSchedule::getCourseId, sameSemesterCourseIds)
        );
        List<CourseSchedule> conflicts = new ArrayList<>();
        for (CourseSchedule targetSchedule : targetSchedules) {
            for (CourseSchedule selectedSchedule : selectedSchedules) {
                if (isScheduleOverlap(targetSchedule, selectedSchedule)) {
                    conflicts.add(selectedSchedule);
                }
            }
        }
        return conflicts;
    }

    private boolean isScheduleOverlap(CourseSchedule a, CourseSchedule b) {
        return a.getWeekday().equals(b.getWeekday())
                && a.getSection().equals(b.getSection())
                && a.getEndWeek() >= b.getStartWeek()
                && a.getStartWeek() <= b.getEndWeek();
    }

    @Override
    public Result<Void> dropCourse(Long sid, Long cid) {
        verifyStudent(sid);
        verifyCourse(cid, false, true);
        CourseSelection selection = getSelection(sid, cid);
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else {
            if (selection.getStatus() == SelectionStatus.DROPPED) {
                return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
            } else if (selection.getStatus() == SelectionStatus.OVER) {
                return Result.error(ResultCode.COURSE_ALREADY_OVER);
            } else {
                selection.setStatus(SelectionStatus.DROPPED);
                selection.setUpdateTime(LocalDateTime.now());
                selectionMapper.updateById(selection);
                return Result.success();
            }
        }
    }

    @Override
    public Result<Void> endCourse(Long tid, EndCourseDTO dto) {
        verifyCourse(dto.getCid(), false, true);
        verifyTeacher(dto.getCid(), tid);
        CourseSelection selection = getSelection(dto.getSid(), dto.getCid());
        if (selection == null) {
            return Result.error(ResultCode.SELECTION_NOT_FOUND);
        } else if (selection.getStatus() == SelectionStatus.DROPPED) {
            return Result.error(ResultCode.COURSE_ALREADY_DROPPED);
        } else {
            double score = dto.getScore();
            if (score > 100 || score < 0) {
                return Result.error(ResultCode.INVALID_SCORE);
            }
            double precision = 1e-2;
            double roundedScore = (double) Math.round(score / precision) * precision;
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

    @Transactional
    @Override
    public Result<Void> endCourseBatch(Long tid, EndCourseBatchDTO dto) {
        verifyCourse(dto.getCourseId(), false, true);
        verifyTeacher(dto.getCourseId(), tid);
        for (EndCourseBatchDTO.Item item : dto.getItems()) {
            CourseSelection selection = getSelection(item.getStudentId(), dto.getCourseId());
            if (selection == null) {
                throw new BusinessException(ResultCode.SELECTION_NOT_FOUND);
            } else if (selection.getStatus() == SelectionStatus.DROPPED) {
                throw new BusinessException(ResultCode.COURSE_ALREADY_DROPPED);
            }
            validateScore(item.getScore());
            selection.setStatus(SelectionStatus.OVER);
            selection.setScore(item.getScore());
            selection.setUpdateTime(LocalDateTime.now());
            selectionMapper.updateById(selection);
        }
        return Result.success();
    }

    @Override
    public Result<SelectionCheckVO> checkSelectable(Long sid, Long cid) {
        verifyStudent(sid);
        Course course = courseMapper.selectById(cid);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        return Result.success(buildSelectionCheck(sid, course));
    }

    private void validateScore(Double score) {
        if (score > 100 || score < 0) {
            throw new BusinessException(ResultCode.INVALID_SCORE);
        }
        double precision = 1e-2;
        double roundedScore = (double) Math.round(score / precision) * precision;
        if (roundedScore != score) {
            throw new BusinessException(ResultCode.INVALID_SCORE);
        }
    }

    private CourseVO selectionToCourseVO(CourseSelection selection) {
        CourseVO vo = new CourseVO();
        Course course = courseMapper.selectById(selection.getCourseId());
        BeanUtils.copyProperties(course, vo);
        vo.setStatus(course.getStatus().getValue());
        User teacher = userMapper.selectById(course.getTeacherId());
        vo.setTeacherName(teacher.getName());
        vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
        vo.setSize(currentSize(course.getId()));
        return vo;
    }

    private CourseVO selectionToAvailableCourseVO(Long sid, Course course) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);
        vo.setStatus(course.getStatus().getValue());
        User teacher = userMapper.selectById(course.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getName());
            vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
        }
        vo.setSize(currentSize(course.getId()));
        CourseSelection selection = getSelection(sid, course.getId());
        if (selection != null) {
            vo.setMySelectionStatus(selection.getStatus());
        }
        SelectionCheckVO check = buildSelectionCheck(sid, course);
        vo.setSelectable(check.getSelectable());
        vo.setUnselectableReason(check.getReason());
        return vo;
    }

    private SelectionCheckVO buildSelectionCheck(Long sid, Course course) {
        SelectionCheckVO vo = new SelectionCheckVO();
        vo.setConflictSchedules(List.of());
        CourseSelection selection = getSelection(sid, course.getId());
        if (selection != null && selection.getStatus() == SelectionStatus.PICKED) {
            return unselectable(ResultCode.COURSE_ALREADY_PICKED, List.of());
        }
        if (selection != null && selection.getStatus() == SelectionStatus.OVER) {
            return unselectable(ResultCode.COURSE_ALREADY_OVER, List.of());
        }
        if (course.getStatus() == CourseStatus.OFF) {
            return unselectable(ResultCode.COURSE_UNPICKABLE, List.of());
        }
        if (currentSize(course.getId()) >= course.getCapacity()) {
            return unselectable(ResultCode.COURSE_FULL, List.of());
        }
        List<ScheduleVO> conflictSchedules = findScheduleConflicts(sid, course).stream()
                .map(this::scheduleToVO)
                .toList();
        if (!conflictSchedules.isEmpty()) {
            return unselectable(ResultCode.COURSE_TIME_CONFLICT, conflictSchedules);
        }
        vo.setSelectable(true);
        return vo;
    }

    private SelectionCheckVO unselectable(ResultCode resultCode, List<ScheduleVO> conflictSchedules) {
        SelectionCheckVO vo = new SelectionCheckVO();
        vo.setSelectable(false);
        vo.setReasonCode(resultCode.name());
        vo.setReason(resultCode.getMsg());
        vo.setConflictSchedules(conflictSchedules);
        return vo;
    }

    private ScheduleVO scheduleToVO(CourseSchedule schedule) {
        Course course = courseMapper.selectById(schedule.getCourseId());
        Classroom classroom = classroomMapper.selectById(schedule.getClassroomId());
        User teacher = course == null ? null : userMapper.selectById(course.getTeacherId());
        ScheduleVO vo = new ScheduleVO();
        vo.setId(schedule.getId());
        vo.setCourseId(schedule.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getName());
        }
        if (teacher != null) {
            vo.setTeacherName(teacher.getName());
            vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
        }
        vo.setWeekday(schedule.getWeekday());
        vo.setSection(schedule.getSection());
        vo.setStartWeek(schedule.getStartWeek());
        vo.setEndWeek(schedule.getEndWeek());
        if (classroom != null) {
            vo.setClassroomId(classroom.getId());
            vo.setClassroomName(classroom.getName());
        }
        return vo;
    }

    private ScoreVO selectionToScoreVO(CourseSelection selection) {
        ScoreVO vo = new ScoreVO();
        BeanUtils.copyProperties(selectionToCourseVO(selection), vo);
        vo.setScore(selection.getScore());
        vo.setGPA(Math.max(0.0, (double) Math.round(selection.getScore() - 50) / 10));
        BeanUtils.copyProperties(getScoreInfo(selection.getScore(), selection.getCourseId()), vo);
        return vo;
    }

    @Override
    public Result<IPage<CourseVO>> showSelectedList(Long sid, SelectionQueryDTO dto) {
        verifyStudent(sid);
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid).eq(CourseSelection::getStatus, SelectionStatus.PICKED)
                .orderByAsc(CourseSelection::getCourseId);
        Page<CourseSelection> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return Result.success(selectionMapper.selectPage(page, wrapper).convert(this::selectionToCourseVO));
    }

    private ScoreDetailDTO getScoreInfo(Double score, Long cid) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid)
                .eq(CourseSelection::getStatus, SelectionStatus.OVER);
        List<CourseSelection> originalList = selectionMapper.selectList(wrapper);
        List<Double> list = new ArrayList<>();
        for (CourseSelection selection : originalList) {
            list.add(selection.getScore());
        }
        list.sort(Comparator.reverseOrder());
        List<Double> distinctList = new ArrayList<>(new HashSet<>(list));
        distinctList.sort(Comparator.reverseOrder());
        return new ScoreDetailDTO(
                list.getFirst(), list.getLast(), list.size(),
                list.indexOf(score) + 1, distinctList.indexOf(score) + 1
        );
    }

    @Override
    public Result<IPage<ScoreVO>> showScoreList(Long sid, ScoreQueryDTO dto) {
        verifyStudent(sid);
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid).eq(CourseSelection::getStatus, SelectionStatus.OVER)
                .exists("SELECT 1 FROM course c WHERE c.id = course_selection.course_id " +
                        "AND c.semester = {0}", dto.getSemester())
                .orderByAsc(CourseSelection::getCourseId);
        Page<CourseSelection> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return Result.success(
                selectionMapper.selectPage(page, wrapper).convert(this::selectionToScoreVO)
        );
    }

    @Override
    public Result<ScoreVO> getScoreDetail(Long sid, Long cid) {
        verifyStudent(sid);
        verifyCourse(cid, true, true);
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, sid).eq(CourseSelection::getCourseId, cid);
        CourseSelection selection = selectionMapper.selectOne(wrapper);
        if (selection == null) {
            throw new BusinessException(ResultCode.SELECTION_NOT_FOUND);
        } else if (selection.getScore() == null) {
            throw new BusinessException(ResultCode.SCORE_UNAVAILABLE);
        }
        return Result.success(selectionToScoreVO(selection));
    }
}
