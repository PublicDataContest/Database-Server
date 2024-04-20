package com.example.publicdataserver.service;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.statistics.SeasonsStatistics;
import com.example.publicdataserver.dto.StatisticsDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class StatisticsService {

    @Autowired
    private PublicDataRepository publicDataRepository;

    @Autowired
    private StatisticsRepository seasonsStatisticsRepository;

    @Transactional
    public void updateSeasonStatistics(String execLoc) {
        try{
            log.debug("execLoc : " + execLoc);
            List<PublicData> publicDatas = publicDataRepository.findPublicDataByExecLoc(execLoc);
            StatisticsDto statisticsDto = new StatisticsDto(0L, 0L, 0L, 0L);

            for (PublicData data : publicDatas) {
                switch (data.getExecMonth()) {
                    case "03": case "04": case "05":
                        statisticsDto.setSpring(statisticsDto.getSpring() + 1);
                        break;
                    case "06": case "07": case "08":
                        statisticsDto.setSummer(statisticsDto.getSummer() + 1);
                        break;
                    case "09": case "10": case "11":
                        statisticsDto.setFall(statisticsDto.getFall() + 1);
                        break;
                    case "12": case "01": case "02":
                        statisticsDto.setWinter(statisticsDto.getWinter() + 1);
                        break;
                }
            }

            SeasonsStatistics statistics = convertDTOToEntity(statisticsDto);
            seasonsStatisticsRepository.save(statistics);
            log.info("저장된 통계:" + statistics);
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }

    private SeasonsStatistics convertDTOToEntity(StatisticsDto dto) {
        return SeasonsStatistics.builder()
                .spring(dto.getSpring())
                .summer(dto.getSummer())
                .fall(dto.getFall())
                .winter(dto.getWinter())
                .build();
    }
}