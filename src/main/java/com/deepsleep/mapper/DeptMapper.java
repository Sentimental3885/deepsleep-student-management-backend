package com.deepsleep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepsleep.data.po.Dept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
}
