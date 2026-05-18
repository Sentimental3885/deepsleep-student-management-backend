package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Teacher {
    @TableId(value = "user_id",type = IdType.INPUT)
    private Long userId;

    private Long deptId;
    private String title;
    private LocalDate entryDate;
}
