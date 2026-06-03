package com.deepsleep.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepsleep.data.dto.ClazzDTO;
import com.deepsleep.data.dto.DeptDTO;
import com.deepsleep.data.dto.MajorDTO;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.po.*;
import com.deepsleep.data.vo.ClazzVO;
import com.deepsleep.data.vo.DeptVO;
import com.deepsleep.data.vo.MajorVO;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.*;
import com.deepsleep.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final DeptMapper deptMapper;
    private final MajorMapper majorMapper;
    private final ClazzMapper clazzMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    @Override
    public void addDept(DeptDTO dto) {
        Long count = deptMapper.selectCount(
                new LambdaQueryWrapper<Dept>().eq(Dept::getName, dto.getName())
        );
        if (count > 0) throw new BusinessException(ResultCode.DEPT_CONFLICT);
        Dept dept = new Dept();
        dept.setName(dto.getName());
        deptMapper.insert(dept);
    }

    @Override
    public void updateDept(Long id, DeptDTO dto) {
        Dept dept = deptMapper.selectById(id);
        if (dept==null) throw new BusinessException(ResultCode.NOT_FOUND);

        Long count = deptMapper.selectCount(
                new LambdaQueryWrapper<Dept>()
                        .eq(Dept::getName, dto.getName())
                        .ne(Dept::getId, id)
        );
        if (count > 0) throw new BusinessException(ResultCode.DEPT_CONFLICT);

        dept.setName(dto.getName());
        deptMapper.updateById(dept);
    }

    @Override
    public void deleteDept(Long id) {
        // 检查是否有专业、学生、教师已关联
        Long majorCount = majorMapper.selectCount(
                new LambdaQueryWrapper<Major>().eq(Major::getDeptId, id));
        if (majorCount > 0) throw new BusinessException(ResultCode.DEPT_HAS_REFERENCES);

        Long studentCount = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getDeptId, id));
        if (studentCount > 0) throw new BusinessException(ResultCode.DEPT_HAS_REFERENCES);

        Long teacherCount = teacherMapper.selectCount(
                new LambdaQueryWrapper<Teacher>().eq(Teacher::getDeptId, id));
        if (teacherCount > 0) throw new BusinessException(ResultCode.DEPT_HAS_REFERENCES);

        deptMapper.deleteById(id);
    }

    @Override
    public List<DeptVO> listDepts() {
        return deptMapper.selectList(null).stream().map(dept -> {
            DeptVO vo = new DeptVO();
            vo.setId(dept.getId());
            vo.setName(dept.getName());
            return vo;
        }).toList();
    }

    @Override
    public void addMajor(MajorDTO dto) {
        if (deptMapper.selectById(dto.getDeptId())==null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Long count = majorMapper.selectCount(
                new LambdaQueryWrapper<Major>()
                        .eq(Major::getDeptId, dto.getDeptId())
                        .eq(Major::getName, dto.getName())
        );
        if (count>0) throw new BusinessException(ResultCode.MAJOR_CONFLICT);

        Major major = new Major();
        major.setName(dto.getName());
        major.setDeptId(dto.getDeptId());
        majorMapper.insert(major);
    }

    @Override
    public void updateMajor(Long id, MajorDTO dto) {
        Major major = majorMapper.selectById(id);
        if (major==null) throw new BusinessException(ResultCode.NOT_FOUND);
        if (deptMapper.selectById(dto.getDeptId())==null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        Long count = majorMapper.selectCount(
                new LambdaQueryWrapper<Major>()
                        .eq(Major::getDeptId, dto.getDeptId())
                        .eq(Major::getName, dto.getName())
                        .ne(Major::getId, id)
        );
        if (count > 0) throw new BusinessException(ResultCode.MAJOR_CONFLICT);

        major.setName(dto.getName());
        major.setDeptId(dto.getDeptId());
        majorMapper.updateById(major);
    }

    @Override
    public void deleteMajor(Long id) {
        Long clazzCount = clazzMapper.selectCount(
                new LambdaQueryWrapper<Clazz>().eq(Clazz::getMajorId, id));
        if (clazzCount>0) throw new BusinessException(ResultCode.MAJOR_HAS_REFERENCES);

        Long studentCount = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getMajorId, id));
        if (studentCount>0) throw new BusinessException(ResultCode.MAJOR_HAS_REFERENCES);

        majorMapper.deleteById(id);
    }

    @Override
    public List<MajorVO> listMajors(Long deptId) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        if (deptId!=null) wrapper.eq(Major::getDeptId, deptId);

        return majorMapper.selectList(wrapper).stream().map(major -> {
            MajorVO vo = new MajorVO();
            vo.setId(major.getId());
            vo.setName(major.getName());
            vo.setDeptId(major.getDeptId());
            Dept dept = deptMapper.selectById(major.getDeptId());
            if (dept != null) vo.setDeptName(dept.getName());
            return vo;
        }).toList();
    }


    @Override
    public void addClazz(ClazzDTO dto) {
        if (deptMapper.selectById(dto.getDeptId())==null || majorMapper.selectById(dto.getMajorId())==null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Long count = clazzMapper.selectCount(
                new LambdaQueryWrapper<Clazz>().eq(Clazz::getName,dto.getName())
        );
        if (count > 0) throw new BusinessException(ResultCode.CLAZZ_CONFLICT);

        Clazz clazz = new Clazz();
        clazz.setName(dto.getName());
        clazz.setDeptId(dto.getDeptId());
        clazz.setMajorId(dto.getMajorId());
        clazz.setGrade(dto.getGrade());
        clazzMapper.insert(clazz);
    }

    @Override
    public void updateClazz(Long id, ClazzDTO dto) {
        Clazz clazz = clazzMapper.selectById(id);
        if (clazz==null) throw new BusinessException(ResultCode.NOT_FOUND);

        Long count = clazzMapper.selectCount(
                new LambdaQueryWrapper<Clazz>()
                        .eq(Clazz::getName, dto.getName())
                        .ne(Clazz::getId, id)
        );
        if (count>0) throw new BusinessException(ResultCode.CLAZZ_CONFLICT);

        clazz.setName(dto.getName());
        clazz.setDeptId(dto.getDeptId());
        clazz.setMajorId(dto.getMajorId());
        clazz.setGrade(dto.getGrade());
        clazzMapper.updateById(clazz);
    }

    @Override
    public void deleteClazz(Long id) {
        Long studentCount = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getClazzId, id));
        if (studentCount>0) throw new BusinessException(ResultCode.CLAZZ_HAS_REFERENCES);
        clazzMapper.deleteById(id);
    }

    @Override
    public List<ClazzVO> listClazzes(Long majorId) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        if (majorId!=null) wrapper.eq(Clazz::getMajorId, majorId);

        return clazzMapper.selectList(wrapper).stream().map(clazz -> {
            ClazzVO vo = new ClazzVO();
            vo.setId(clazz.getId());
            vo.setName(clazz.getName());
            vo.setDeptId(clazz.getDeptId());
            vo.setMajorId(clazz.getMajorId());
            vo.setGrade(clazz.getGrade());
            Dept dept = deptMapper.selectById(clazz.getDeptId());
            if (dept!=null) vo.setDeptName(dept.getName());
            Major major = majorMapper.selectById(clazz.getMajorId());
            if (major!=null) vo.setMajorName(major.getName());
            return vo;
        }).toList();
    }
}
