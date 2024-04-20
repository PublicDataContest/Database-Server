package com.example.publicdataserver.service.statistics;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.statistics.TimeStatistics;
import com.example.publicdataserver.dto.statistic.TimeStatisticsDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.statistics.TimeStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TimeStatisticsService {
    @Autowired
    private PublicDataRepository publicDataRepository;
    @Autowired
    private TimeStatisticsRepository timeStatisticsRepository;

    @Transactional
    public void updateTimeStatistics(String exeLoc, Long restaurantId){
        List<PublicData> publicData = publicDataRepository.findPublicDataByExecLoc(exeLoc);
        Optional<TimeStatistics> timeStatistics=timeStatisticsRepository.findByRestaurantId(restaurantId);
        final TimeStatisticsDto timeStatisticsDto = timeStatistics
                .map(this::convertEntityToDto)
                .orElseGet(() -> TimeStatisticsDto.builder()
                        .hour8(0L).hour9(0L).hour10(0L).hour11(0L).hour12(0L)
                        .hour13(0L).hour14(0L).hour15(0L).hour16(0L).hour17(0L)
                        .hour18(0L).hour19(0L).hour20(0L).hour21(0L)
                        .build());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        publicData.forEach(data -> {
            LocalDateTime dateTime = LocalDateTime.parse(data.getExecDt(), formatter);
            int hour = dateTime.getHour();

            switch (hour) {
                case 8: timeStatisticsDto.setHour8(timeStatisticsDto.getHour8() + 1);break;
                case 9: timeStatisticsDto.setHour9(timeStatisticsDto.getHour9() + 1); break;
                case 10: timeStatisticsDto.setHour10(timeStatisticsDto.getHour10() + 1); break;
                case 11: timeStatisticsDto.setHour11(timeStatisticsDto.getHour11() + 1); break;
                case 12: timeStatisticsDto.setHour12(timeStatisticsDto.getHour12() + 1); break;
                case 13: timeStatisticsDto.setHour13(timeStatisticsDto.getHour13() + 1); break;
                case 14: timeStatisticsDto.setHour14(timeStatisticsDto.getHour14() + 1); break;
                case 15: timeStatisticsDto.setHour15(timeStatisticsDto.getHour15() + 1); break;
                case 16: timeStatisticsDto.setHour16(timeStatisticsDto.getHour16() + 1); break;
                case 17: timeStatisticsDto.setHour17(timeStatisticsDto.getHour17() + 1); break;
                case 18: timeStatisticsDto.setHour18(timeStatisticsDto.getHour18() + 1); break;
                case 19: timeStatisticsDto.setHour19(timeStatisticsDto.getHour19() + 1); break;
                case 20: timeStatisticsDto.setHour20(timeStatisticsDto.getHour20() + 1); break;
                case 21: timeStatisticsDto.setHour21(timeStatisticsDto.getHour21() + 1); break;
            }
        });
        TimeStatistics statistics=convertDtoToEntity(timeStatisticsDto,restaurantId);
        timeStatisticsRepository.save(statistics);
    }

    private TimeStatisticsDto convertEntityToDto(TimeStatistics entity){
        return TimeStatisticsDto.builder()
                .hour8(entity.getHour8())
                .hour9(entity.getHour9())
                .hour10(entity.getHour10())
                .hour11(entity.getHour11())
                .hour12(entity.getHour12())
                .hour13(entity.getHour13())
                .hour14(entity.getHour14())
                .hour15(entity.getHour15())
                .hour16(entity.getHour16())
                .hour17(entity.getHour17())
                .hour18(entity.getHour18())
                .hour19(entity.getHour19())
                .hour20(entity.getHour20())
                .hour21(entity.getHour21())
                .build();
    }

    private TimeStatistics convertDtoToEntity(TimeStatisticsDto dto, Long restaurantId){
        return TimeStatistics.builder()
                .hour8(dto.getHour8())
                .hour9(dto.getHour9())
                .hour10(dto.getHour10())
                .hour11(dto.getHour11())
                .hour12(dto.getHour12())
                .hour13(dto.getHour13())
                .hour14(dto.getHour14())
                .hour15(dto.getHour15())
                .hour16(dto.getHour16())
                .hour17(dto.getHour17())
                .hour18(dto.getHour18())
                .hour19(dto.getHour19())
                .hour20(dto.getHour20())
                .hour21(dto.getHour21())
                .restaurantId(restaurantId)
                .build();
    }
}
