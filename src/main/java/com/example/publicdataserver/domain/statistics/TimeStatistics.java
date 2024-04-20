package com.example.publicdataserver.domain.statistics;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TimeStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long restaurantId;

    @ColumnDefault("0")
    private Long hour8;

    @ColumnDefault("0")
    private Long hour9;

    @ColumnDefault("0")
    private Long hour10;

    @ColumnDefault("0")
    private Long hour11;

    @ColumnDefault("0")
    private Long hour12;

    @ColumnDefault("0")
    private Long hour13;

    @ColumnDefault("0")
    private Long hour14;

    @ColumnDefault("0")
    private Long hour15;

    @ColumnDefault("0")
    private Long hour16;

    @ColumnDefault("0")
    private Long hour17;

    @ColumnDefault("0")
    private Long hour18;

    @ColumnDefault("0")
    private Long hour19;

    @ColumnDefault("0")
    private Long hour20;

    @ColumnDefault("0")
    private Long hour21;
}
