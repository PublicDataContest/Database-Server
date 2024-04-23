package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.PublicData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PublicDataRepository extends JpaRepository<PublicData, Long> {
    List<PublicData> findPublicDataByExecLoc(String execLoc);

    @Query("SELECT SUM(pd.execAmount) FROM PublicData pd WHERE pd.execLoc = :execLoc")
    BigDecimal sumExecAmountByExecLoc(@Param("execLoc") String execLoc);

    @Query("SELECT COUNT(pd) FROM PublicData pd WHERE pd.execLoc = :execLoc")
    Long countByExecLoc(@Param("execLoc") String execLoc);

    List<PublicData> findAllByOrderByExecDtDesc();
}
