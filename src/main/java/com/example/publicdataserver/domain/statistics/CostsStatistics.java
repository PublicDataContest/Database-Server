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
public class CostsStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long restaurantId;


    @ColumnDefault("0")
    private Long lower10000;
    @ColumnDefault("0")
    private Long lower15000;
    @ColumnDefault("0")
    private Long lower20000;
    @ColumnDefault("0")
    private Long upper20000;

}
