package com.example.publicdataserver.repository.statistics;

import com.example.publicdataserver.domain.statistics.PeopleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleStatisticsRepository extends JpaRepository<PeopleStatistics,Long> {
    Optional<PeopleStatistics> findByRestaurantId(Long restaurantId);
}
