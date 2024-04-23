package com.example.publicdataserver.service.statistics;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.statistics.CostsStatistics;
import com.example.publicdataserver.domain.statistics.PeopleStatistics;
import com.example.publicdataserver.dto.statistic.CostStatisticsDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.statistics.CostStatisticsRepository;
import com.example.publicdataserver.repository.statistics.PeopleStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CostStatisticsService {

    @Autowired
    private PublicDataRepository publicDataRepository;

    @Autowired
    private CostStatisticsRepository costStatisticsRepository;
    @Autowired
    private PeopleStatisticsRepository peopleStatisticsRepository;


    @Transactional
    public void updateCostStatistics(String execLoc, Long restaurantId) {
        List<PublicData> publicData = publicDataRepository.findPublicDataByExecLoc(execLoc);
        Optional<CostsStatistics> costsStatistics = costStatisticsRepository.findByRestaurantId(restaurantId);
        CostStatisticsDto costStatisticsDto = null;
        if (costsStatistics.isEmpty()) {
            costStatisticsDto = costStatisticsDto.builder()
                    .lower10000(0L)
                    .lower15000(0L)
                    .lower20000(0L)
                    .upper20000(0L)
                    .build();
        } else {
            costStatisticsDto = convertEntityToDto(costsStatistics.get());
        }
        Optional<PeopleStatistics> peopleStatistics=peopleStatisticsRepository.findByRestaurantId(restaurantId);
        long totalPeople=peopleStatistics.get().getLower5()+peopleStatistics.get().getLower10()
                +peopleStatistics.get().getLower20()+peopleStatistics.get().getUpper20();

        for(PublicData data:publicData){
            BigDecimal execAmountDecimal = data.getExecAmount(); // BigDecimal 객체를 가져옵니다.
            long execAmount = execAmountDecimal.longValue(); // BigDecimal을 long으로 변환합니다.

            long cost = execAmount / totalPeople;

            if(cost<=10000){
                costStatisticsDto.setLower10000(costStatisticsDto.getLower10000() + 1);
            }
            else if(cost<=15000){
                costStatisticsDto.setLower15000(costStatisticsDto.getLower15000() + 1);
            }
            else if(cost<=20000){
                costStatisticsDto.setLower20000(costStatisticsDto.getLower20000() + 1);
            }
            else{
                costStatisticsDto.setUpper20000(costStatisticsDto.getUpper20000() + 1);
            }

        }
        CostsStatistics statistics=convertDtoToEntity(costStatisticsDto,restaurantId);
        costStatisticsRepository.save(statistics);
    }

    private CostStatisticsDto convertEntityToDto(CostsStatistics entity){
        return CostStatisticsDto.builder()
                .lower10000(entity.getLower10000())
                .lower15000(entity.getLower15000())
                .lower20000(entity.getLower20000())
                .upper20000(entity.getUpper20000())
                .build();
    }

    private CostsStatistics convertDtoToEntity(CostStatisticsDto dto,Long restaurantId){
        return CostsStatistics.builder()
                .lower10000(dto.getLower10000())
                .lower15000(dto.getLower15000())
                .lower20000(dto.getLower20000())
                .upper20000(dto.getUpper20000())
                .restaurantId(restaurantId)
                .build();
    }
}
