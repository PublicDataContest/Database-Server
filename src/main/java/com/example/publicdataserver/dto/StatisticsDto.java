package com.example.publicdataserver.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDto {
    private Long spring;
    private Long summer;
    private Long fall;
    private Long winter;
}
