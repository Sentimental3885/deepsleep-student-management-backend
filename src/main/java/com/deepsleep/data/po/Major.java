package com.deepsleep.data.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Major {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String name;
    private Long deptId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
