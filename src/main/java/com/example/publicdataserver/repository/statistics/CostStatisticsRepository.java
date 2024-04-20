package com.example.publicdataserver.repository.statistics;

import com.example.publicdataserver.domain.statistics.CostsStatistics;
import com.example.publicdataserver.domain.statistics.SeasonsStatistics;
import com.example.publicdataserver.dto.statistic.CostStatisticsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CostStatisticsRepository extends JpaRepository<CostsStatistics,Long> {
    Optional<CostsStatistics> findByRestaurantId(Long restaurantId);

}
