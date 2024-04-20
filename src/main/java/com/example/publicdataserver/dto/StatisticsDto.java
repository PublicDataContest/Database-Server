package com.example.publicdataserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private Long spring;
    private Long summer;
    private Long fall;
    private Long winter;
}
