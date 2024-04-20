package com.example.publicdataserver.service.Statistics;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.statistics.SeasonsStatistics;
import com.example.publicdataserver.dto.statistic.SeasonStatisticsDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.statistics.SeasonStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class SeasonStatisticsService {

    @Autowired
    private PublicDataRepository publicDataRepository;

    @Autowired
    private SeasonStatisticsRepository seasonsStatisticsRepository;

    @Transactional
    public void updateSeasonStatistics(String execLoc, Long restaurantId) {
        try{
            List<PublicData> publicDatas = publicDataRepository.findPublicDataByExecLoc(execLoc); // public Datas 찾고
            Optional<SeasonsStatistics> seasonsStatistics = seasonsStatisticsRepository.findByRestaurantId(restaurantId); // 해당 Restaurant에 대한 통계 찾고
            SeasonStatisticsDto statisticsDto = null;
            if(seasonsStatistics.isEmpty()) {
                statisticsDto = statisticsDto.builder()
                        .spring(0L)
                        .summer(0L)
                        .fall(0L)
                        .winter(0L)
                        .build();
            } else {
                statisticsDto = convertEntityToEDto(seasonsStatistics.get());
            }

            for (PublicData data : publicDatas) { // data 돌면서 갱신
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

            SeasonsStatistics statistics = convertDTOToEntity(statisticsDto, restaurantId);
            seasonsStatisticsRepository.save(statistics);
            log.info("저장된 통계:" + statistics);
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }

    private SeasonStatisticsDto convertEntityToEDto(SeasonsStatistics entity) {
        return SeasonStatisticsDto.builder()
                .spring(entity.getSpring())
                .summer(entity.getSummer())
                .fall(entity.getFall())
                .winter(entity.getWinter())
                .build();
    }

    private SeasonsStatistics convertDTOToEntity(SeasonStatisticsDto dto, Long restaurantId) {
        return SeasonsStatistics.builder()
                .spring(dto.getSpring())
                .summer(dto.getSummer())
                .fall(dto.getFall())
                .winter(dto.getWinter())
                .restaurantId(restaurantId)
                .build();
    }
}