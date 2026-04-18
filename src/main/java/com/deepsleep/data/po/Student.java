package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    //主键用@TableId注明
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    private Long deptId;
    private Long majorId;
    private Long clazzId;
    private String position;
    private LocalDate entryDate;
}
