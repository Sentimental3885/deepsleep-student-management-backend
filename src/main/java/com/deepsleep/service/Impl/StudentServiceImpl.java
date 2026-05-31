package com.deepsleep.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.UpdateStudentDTO;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.*;
import com.deepsleep.data.vo.ScheduleVO;
import com.deepsleep.data.vo.StudentProfileVO;
import com.deepsleep.mapper.*;
import com.deepsleep.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {


    private final StudentMapper studentMapper;
    private final DeptMapper deptMapper;
    private final MajorMapper majorMapper;
    private final ClazzMapper clazzMapper;
    private final CourseSelectionMapper selectionMapper;
    private final UserMapper userMapper;
    private final ClassroomMapper classroomMapper;
    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseMapper courseMapper;

    @Override
    public StudentProfileVO getStudentProfile() {
        Long userId = UserContext.getUserId();
        Student student = studentMapper.selectById(userId);
        StudentProfileVO vo = new StudentProfileVO();
        vo.setDeptId(student.getDeptId());
        vo.setMajorId(student.getMajorId());
        vo.setClazzId(student.getClazzId());
        vo.setPosition(student.getPosition());
        vo.setEntryDate(student.getEntryDate());
        vo.setDeptName(deptMapper.selectById(student.getDeptId()).getName());
        vo.setMajorName(majorMapper.selectById(student.getMajorId()).getName());
        vo.setClazzName(clazzMapper.selectById(student.getClazzId()).getName());
        return vo;
    }

    @Override
    public void updateStudentInfo(UpdateStudentDTO dto) {
        Long userId = UserContext.getUserId();
        Student student = new Student();
        student.setUserId(userId);
        student.setClazzId(dto.getClazzId());
        student.setPosition(dto.getPosition());
        student.setEntryDate(dto.getEntryDate());
        studentMapper.updateById(student);
    }

    @Override
    public List<ScheduleVO> getMySchedule() {
        Long userId = UserContext.getUserId();
        List<CourseSelection> selections = selectionMapper.selectList(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getStudentId, userId)
                        .ne(CourseSelection::getStatus, SelectionStatus.DROPPED)
        );
        if (selections.isEmpty()) return List.of();

        List<Long> courseIds = selections.stream().map(CourseSelection::getCourseId)
                .toList();

        // 查这些课程的所有排课记录
        List<CourseSchedule> schedules = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>()
                        .in(CourseSchedule::getCourseId, courseIds)
        );

        return schedules.stream().map(schedule -> {
            ScheduleVO vo = new ScheduleVO();
            Course course = courseMapper.selectById(schedule.getCourseId());
            Classroom classroom = classroomMapper.selectById(schedule.getClassroomId());
            User teacher = userMapper.selectById(course.getTeacherId());
            vo.setCourseId(course.getId());
            vo.setCourseName(course.getName());
            vo.setTeacherName(teacher.getName());
            vo.setWeekday(schedule.getWeekday());
            vo.setSection(schedule.getSection());
            vo.setStartWeek(schedule.getStartWeek());
            vo.setEndWeek(schedule.getEndWeek());
            vo.setClassroomId(classroom.getId());
            vo.setClassroomName(classroom.getName());
            return vo;
        }).toList();
    }


}
