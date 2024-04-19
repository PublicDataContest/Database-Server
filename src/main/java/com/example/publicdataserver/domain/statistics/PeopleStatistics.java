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
public class PeopleStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("0")
    private Long lower5;
    @ColumnDefault("0")
    private Long lower10;
    @ColumnDefault("0")
    private Long lower20;
    @ColumnDefault("0")
    private Long upper20;
}
