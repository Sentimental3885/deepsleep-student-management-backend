package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.ClassroomScheduleQueryDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.Classroom;
import com.deepsleep.data.po.Course;
import com.deepsleep.data.po.CourseSchedule;
import com.deepsleep.data.po.User;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.ClassroomMapper;
import com.deepsleep.mapper.CourseMapper;
import com.deepsleep.mapper.CourseScheduleMapper;
import com.deepsleep.mapper.UserMapper;
import com.deepsleep.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final CourseScheduleMapper scheduleMapper;
    private final CourseMapper courseMapper;
    private final ClassroomMapper classroomMapper;
    private final UserMapper userMapper;
    private final FileStorage fileStorage;

    @Override
    public List<ScheduleVO> getClassroomSchedule(Long classroomId, ClassroomScheduleQueryDTO dto) {
        Classroom classroom = classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new BusinessException(ResultCode.CLASSROOM_NOT_FOUND);
        }

        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getClassroomId, classroomId)
                .eq(dto.getWeekday() != null, CourseSchedule::getWeekday, dto.getWeekday())
                .ge(dto.getStartWeek() != null, CourseSchedule::getEndWeek, dto.getStartWeek())
                .le(dto.getEndWeek() != null, CourseSchedule::getStartWeek, dto.getEndWeek())
                .exists(dto.getSemester() != null && !dto.getSemester().isBlank(),
                        "SELECT 1 FROM course c WHERE c.id = course_schedule.course_id AND c.semester = {0}",
                        dto.getSemester())
                .orderByAsc(CourseSchedule::getWeekday)
                .orderByAsc(CourseSchedule::getSection)
                .orderByAsc(CourseSchedule::getStartWeek);
        return scheduleMapper.selectList(wrapper).stream()
                .map(schedule -> toScheduleVO(schedule, classroom))
                .toList();
    }

    private ScheduleVO toScheduleVO(CourseSchedule schedule, Classroom classroom) {
        Course course = courseMapper.selectById(schedule.getCourseId());
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
        vo.setClassroomId(classroom.getId());
        vo.setClassroomName(classroom.getName());
        return vo;
    }
}
