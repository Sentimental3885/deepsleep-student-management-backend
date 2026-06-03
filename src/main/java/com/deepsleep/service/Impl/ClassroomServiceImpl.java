package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.ClassroomDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.Classroom;
import com.deepsleep.data.po.CourseSchedule;
import com.deepsleep.data.po.Exam;
import com.deepsleep.data.vo.ClassroomVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.ClassroomMapper;
import com.deepsleep.mapper.CourseScheduleMapper;
import com.deepsleep.mapper.ExamMapper;
import com.deepsleep.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomMapper classroomMapper;
    private final CourseScheduleMapper courseScheduleMapper;
    private final ExamMapper examMapper;

    @Override
    public void addClassroom(ClassroomDTO dto) {
        Long count = classroomMapper.selectCount(new LambdaQueryWrapper<Classroom>().eq(Classroom::getName,dto.getName()));
        if (count > 0) throw new BusinessException(ResultCode.CLASSROOM_CONFLICT);

        Classroom classroom = new Classroom();
        classroom.setName(dto.getName());
        classroomMapper.insert(classroom);
    }

    @Override
    public void updateClassroom(Long id, ClassroomDTO dto) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom==null) throw new BusinessException(ResultCode.CLASSROOM_NOT_FOUND);
        Long count = classroomMapper.selectCount(
                new LambdaQueryWrapper<Classroom>()
                        .eq(Classroom::getName,dto.getName())
                        .ne(Classroom::getId,id)
        );
        if (count>0) throw new BusinessException(ResultCode.CLASSROOM_CONFLICT);

        classroom.setName(dto.getName());
        classroomMapper.updateById(classroom);
    }

    @Override
    public void deleteClassroom(Long id) {
        if (classroomMapper.selectById(id)==null) {
            throw new BusinessException(ResultCode.CLASSROOM_NOT_FOUND);
        }
        // 是否被排课引用
        Long scheduleCount = courseScheduleMapper.selectCount(
                new LambdaQueryWrapper<CourseSchedule>().eq(CourseSchedule::getClassroomId, id)
        );
        if (scheduleCount > 0) throw new BusinessException(ResultCode.CLASSROOM_HAS_REFERENCES);
        // 是否被考试引用
        Long examCount = examMapper.selectCount(
                new LambdaQueryWrapper<Exam>().eq(Exam::getClassroomId, id)
        );
        if (examCount > 0) throw new BusinessException(ResultCode.CLASSROOM_HAS_REFERENCES);
        classroomMapper.deleteById(id);
    }

    @Override
    public List<ClassroomVO> listClassrooms() {
        return classroomMapper.selectList(null).stream().map(classroom -> {
            ClassroomVO vo = new ClassroomVO();
            vo.setId(classroom.getId());
            vo.setName(classroom.getName());
            return vo;
        }).toList();
    }
}
