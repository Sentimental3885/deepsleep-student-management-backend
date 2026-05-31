package com.deepsleep.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//仿
@Data
@AllArgsConstructor
public class ScoreDetailDTO {
    private Double maxScore;
    private Double minScore;
    private Integer total;
    private Integer rank;
    //去重排名
    private Integer denseRank;
}
