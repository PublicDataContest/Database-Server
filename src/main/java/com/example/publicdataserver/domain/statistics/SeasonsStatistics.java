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
public class SeasonsStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long restaurantId;

    @ColumnDefault("0")
    private Long spring;

    @ColumnDefault("0")
    private Long summer;

    @ColumnDefault("0")
    private Long fall;

    @ColumnDefault("0")
    private Long winter;
}
