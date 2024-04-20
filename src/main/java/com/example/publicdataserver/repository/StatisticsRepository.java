package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.statistics.SeasonsStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<SeasonsStatistics,Long> {
}
