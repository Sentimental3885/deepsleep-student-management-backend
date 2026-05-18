package com.deepsleep.data.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateStudentDTO {
    private Long clazzId;
    private String position;
    private LocalDate entryDate;
}
