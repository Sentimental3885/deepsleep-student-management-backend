package com.deepsleep.data.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Student {
    @TableId(type = IdType.INPUT)
    private Long userId;
    private Long deptId;
    private Long majorId;
    private Long clazzId;
    private String position;
    private LocalDate entryDate;
}