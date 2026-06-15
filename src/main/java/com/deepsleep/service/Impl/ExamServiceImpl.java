package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.dto.AddExamDTO;
import com.deepsleep.data.dto.UpdateExamDTO;
import com.deepsleep.data.enums.ExamType;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.data.enums.SelectionStatus;
import com.deepsleep.data.po.*;
import com.deepsleep.data.vo.ExamVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.infrastructure.file.storage.FileStorage;
import com.deepsleep.mapper.*;
import com.deepsleep.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final FileStorage fileStorage;
    private final ClassroomMapper classroomMapper;
    private final CourseSelectionMapper selectionMapper;

    @Override
    public void addExam(AddExamDTO dto) {
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        Integer role = UserContext.getRole();
        if(role == RoleEnum.TEACHER.getCode()&&!course.getTeacherId().equals(UserContext.getUserId())) throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
        User invigilator = userMapper.selectById(dto.getInvigilatorId());
        if(invigilator == null||invigilator.getRole() != RoleEnum.TEACHER.getCode()){
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }
        if(classroomMapper.selectById(dto.getClassroomId())==null) throw new BusinessException(ResultCode.CLASSROOM_NOT_FOUND);

        Exam exam = new Exam();
        exam.setCourseId(dto.getCourseId());
        exam.setType(dto.getType());
        exam.setExamTime(dto.getExamTime());
        exam.setDuration(dto.getDuration());
        exam.setClassroomId(dto.getClassroomId());
        exam.setInvigilatorId(dto.getInvigilatorId());
        exam.setRemark(dto.getRemark());
        examMapper.insert(exam);
    }

    @Override
    public void updateExam(Long id, UpdateExamDTO dto) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) throw new BusinessException(ResultCode.NOT_FOUND);

        Integer role = UserContext.getRole();
        if(role == RoleEnum.TEACHER.getCode()){
            Course course = courseMapper.selectById(exam.getCourseId());
            if(!course.getTeacherId().equals(UserContext.getUserId())){
                throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
            }
        }
        User invigilator = userMapper.selectById(dto.getInvigilatorId());
        if(invigilator == null || invigilator.getRole() != RoleEnum.TEACHER.getCode()){
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }
        if(classroomMapper.selectById(dto.getClassroomId()) == null) {
            throw new BusinessException(ResultCode.CLASSROOM_NOT_FOUND);
        }

        exam.setType(dto.getType());
        exam.setExamTime(dto.getExamTime());
        exam.setDuration(dto.getDuration());
        exam.setClassroomId(dto.getClassroomId());
        exam.setInvigilatorId(dto.getInvigilatorId());
        exam.setRemark(dto.getRemark());
        examMapper.updateById(exam);
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) throw new BusinessException(ResultCode.NOT_FOUND);

        Integer role = UserContext.getRole();
        if (role == RoleEnum.TEACHER.getCode()){
            Course course = courseMapper.selectById(exam.getCourseId());
            if (!course.getTeacherId().equals(UserContext.getUserId())) {
                throw new BusinessException(ResultCode.TEACHER_UNAUTHORIZED);
            }
        }

        examMapper.deleteById(id);
    }

    @Override
    public ExamVO getExamDetail(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) throw new BusinessException(ResultCode.NOT_FOUND);
        return convertToVO(exam);
    }

    private ExamVO convertToVO(Exam exam) {
        ExamVO vo = new ExamVO();
        vo.setId(exam.getId());
        vo.setCourseId(exam.getCourseId());
        vo.setType(exam.getType());
        vo.setTypeName(ExamType.values()[exam.getType() - 1].getDesc());
        vo.setExamTime(exam.getExamTime());
        vo.setDuration(exam.getDuration());
        vo.setClassroomId(exam.getClassroomId());
        vo.setInvigilatorId(exam.getInvigilatorId());
        vo.setRemark(exam.getRemark());

        Course course = courseMapper.selectById(exam.getCourseId());
        if (course != null) vo.setCourseName(course.getName());
        Classroom classroom = classroomMapper.selectById(exam.getClassroomId());
        if (classroom != null) vo.setClassroomName(classroom.getName());
        User invigilator = userMapper.selectById(exam.getInvigilatorId());
        if (invigilator != null) {
            vo.setInvigilatorName(invigilator.getName());
            vo.setInvigilatorAvatar(fileStorage.getUrl(invigilator.getAvatar()));
        }
        return vo;
    }

    @Override
    public Page<ExamVO> getAllExams(int pageNum, int pageSize) {
        Page<Exam> page = examMapper.selectPage( new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Exam>().orderByDesc(Exam::getExamTime)
        );

        Page<ExamVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).toList());
        return voPage;
    }

    @Override
    public List<ExamVO> getExamsByCourse(Long courseId) {
        if (courseMapper.selectById(courseId) == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        List<Exam> exams = examMapper.selectList(
                new LambdaQueryWrapper<Exam>()
                        .eq(Exam::getCourseId, courseId)
                        .orderByAsc(Exam::getExamTime)
        );
        return exams.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<ExamVO> getMyExamsAsStudent() {
        Long userId = UserContext.getUserId();
        // 查学生选了哪些课
        List<CourseSelection> selections = selectionMapper.selectList( new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getStudentId, userId)
                        .ne(CourseSelection::getStatus, SelectionStatus.DROPPED)
        );

        if (selections.isEmpty()) return List.of();
        List<Long> courseIds = selections.stream().map(CourseSelection::getCourseId).toList();
        List<Exam> exams = examMapper.selectList(
                new LambdaQueryWrapper<Exam>()
                        .in(Exam::getCourseId, courseIds)
                        .orderByAsc(Exam::getExamTime)
        );
        return exams.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<ExamVO> getMyExamsAsInvigilator() {
        Long userId = UserContext.getUserId();
        List<Exam> exams = examMapper.selectList(
                new LambdaQueryWrapper<Exam>().eq(Exam::getInvigilatorId, userId).orderByAsc(Exam::getExamTime)
        );
        return exams.stream().map(this::convertToVO).toList();
    }
}
