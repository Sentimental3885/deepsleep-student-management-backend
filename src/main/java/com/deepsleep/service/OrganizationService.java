package com.deepsleep.service;

import com.deepsleep.data.dto.ClazzDTO;
import com.deepsleep.data.dto.DeptDTO;
import com.deepsleep.data.dto.MajorDTO;
import com.deepsleep.data.vo.ClazzVO;
import com.deepsleep.data.vo.DeptVO;
import com.deepsleep.data.vo.MajorVO;

import java.util.List;

public interface OrganizationService {
    // 学院
    void addDept(DeptDTO dto);
    void updateDept(Long id, DeptDTO dto);
    void deleteDept(Long id);
    List<DeptVO> listDepts();
    // 专业
    void addMajor(MajorDTO dto);
    void updateMajor(Long id, MajorDTO dto);
    void deleteMajor(Long id);
    List<MajorVO> listMajors(Long deptId);
    // 班级
    void addClazz(ClazzDTO dto);
    void updateClazz(Long id, ClazzDTO dto);
    void deleteClazz(Long id);
    List<ClazzVO> listClazzes(Long majorId);
}
