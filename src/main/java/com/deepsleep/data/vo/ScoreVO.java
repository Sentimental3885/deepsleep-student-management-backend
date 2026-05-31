package com.deepsleep.data.vo;

import lombok.Data;

@Data
public class ScoreVO {
    private Long id;
    private String name;
    private String teacherName;
    private String teacherAvatar;
    private String code;
    private String semester;
    private Double credit;
    private Integer status;
    private Double score;
    private Double GPA;
    private Double maxScore;
    private Double minScore;
    private Integer total;
    private Integer rank;
    //位次排名
    private Integer denseRank;
}
