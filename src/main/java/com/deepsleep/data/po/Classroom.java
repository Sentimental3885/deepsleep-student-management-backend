package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("classroom")
public class Classroom {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}