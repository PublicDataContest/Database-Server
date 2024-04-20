package com.example.publicdataserver.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostStatisticsDto {
    private Long lower10000;
    private Long lower15000;
    private Long lower20000;
    private Long upper20000;
}
