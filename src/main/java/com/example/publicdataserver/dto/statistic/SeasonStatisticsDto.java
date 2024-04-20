package com.example.publicdataserver.dto.statistic;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonStatisticsDto {
    private Long spring;
    private Long summer;
    private Long fall;
    private Long winter;
}