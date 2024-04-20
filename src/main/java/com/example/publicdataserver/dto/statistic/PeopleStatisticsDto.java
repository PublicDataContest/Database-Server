package com.example.publicdataserver.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeopleStatisticsDto {

    private Long lower5;
    private Long lower10;
    private Long lower20;
    private Long upper20;
}
