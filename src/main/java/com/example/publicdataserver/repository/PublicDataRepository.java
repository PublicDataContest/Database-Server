package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.PublicData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicDataRepository extends JpaRepository<PublicData, Long> {
    List<PublicData> findPublicDataByExecLoc(String execLoc);
}
