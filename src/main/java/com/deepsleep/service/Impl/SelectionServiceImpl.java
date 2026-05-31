package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.data.dto.EndCourseDTO;
import com.deepsleep.data.dto.ScoreDetailDTO;
import com.deepsleep.data.dto.ScoreQueryDTO;
import com.deepsleep.data.dto.SelectionQueryDTO;
import com.deepsleep.data.enums.CourseStatus;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSelection;
import com.deepsleep.data.po.Student;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.CourseStudentVO;
import com.deepsleep.data.vo.CourseVO;
import com.deepsleep.data.vo.Result;
import com.deepsleep.data.vo.ScoreVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.file.storage.FileStorage;
import com.deepsleep.mapper.CourseMapper;
import com.deepsleep.mapper.CourseSelectionMapper;
import com.deepsleep.mapper.StudentMapper;
import com.deepsleep.mapper.TeacherMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.SelectionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public Long currentSize(Long cid) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, cid);
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
                .exists("SELECT 1 FROM course_clazz cc WHERE cc.course_id = course.id " +
                        "AND cc.clazz_id = {0}", student.getClazzId())
                .notExists("SELECT 1 FROM course_selection cs WHERE cs.student_id = {0} " +
                                "AND course.id = cs.course_id AND cs.status != {1}",
                        sid, SelectionStatus.DROPPED)
                .orderByAsc(Course::getId);
        Page<Course> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return Result.success(
                courseMapper.selectPage(page, wrapper).convert(po -> {
                    CourseVO vo = new CourseVO();
                    BeanUtils.copyProperties(po, vo);
                    vo.setStatus(po.getStatus().getValue());
                    User teacher = userMapper.selectById(po.getTeacherId());
                    vo.setTeacherName(teacher.getName());
                    vo.setTeacherAvatar(fileStorage.getUrl(teacher.getAvatar()));
                    vo.setSize(currentSize(po.getId()));
                    return vo;
                })
        );
    }

    @Override
    public Result<Void> pickCourse(Long sid, Long cid) {
        verifyStudent(sid);
        verifyCourse(cid, false, false);
        CourseSelection selection = getSelection(sid, cid);
        if (selection == null) {
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
                selection.setStatus(SelectionStatus.PICKED);
                selectionMapper.updateById(selection);
                return Result.success();
            }
        }
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
