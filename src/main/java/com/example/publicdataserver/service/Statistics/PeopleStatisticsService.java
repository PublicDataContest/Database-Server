package com.example.publicdataserver.service.Statistics;

import com.example.publicdataserver.domain.PublicData;
import com.example.publicdataserver.domain.statistics.PeopleStatistics;
import com.example.publicdataserver.dto.statistic.PeopleStatisticsDto;
import com.example.publicdataserver.repository.PublicDataRepository;
import com.example.publicdataserver.repository.statistics.PeopleStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PeopleStatisticsService {

    @Autowired
    private PublicDataRepository publicDataRepository;
    @Autowired
    private PeopleStatisticsRepository peopleStatisticsRepository;

    @Transactional
    public void updatePeopleStatistics(String execLoc, Long restaurantId){
        List<PublicData> publicData=publicDataRepository.findPublicDataByExecLoc(execLoc);
        Optional<PeopleStatistics> peopleStatistics=peopleStatisticsRepository.findByRestaurantId(restaurantId);
        PeopleStatisticsDto peopleStatisticsDto=null;
        if(peopleStatistics.isEmpty()){
            peopleStatisticsDto=peopleStatisticsDto.builder()
                    .lower5(0L)
                    .lower10(0L)
                    .lower20(0L)
                    .upper20(0L)
                    .build();
        }
        else{
            peopleStatisticsDto=convertEntityToDto(peopleStatistics.get());
        }
        //숫자만 뽑아내기 위한 문자열 정규식
        Pattern extractNumber=Pattern.compile("\\d+");
        for(PublicData data:publicData){
            //여기에 인원수 로직 처리 구문
            Matcher matcher=extractNumber.matcher(data.getTargetNm());
            if(matcher.find()){
                int convertToNum=Integer.parseInt(matcher.group());
                if(convertToNum<=5){
                    peopleStatisticsDto.setLower5(peopleStatisticsDto.getLower5()+convertToNum+1);
                }
                else if(convertToNum<=10){
                    peopleStatisticsDto.setLower10(peopleStatisticsDto.getLower10()+convertToNum+1);
                }
                else if(convertToNum<=20){
                    peopleStatisticsDto.setLower20(peopleStatisticsDto.getLower20()+convertToNum+1);
                }
                else{
                    peopleStatisticsDto.setUpper20(peopleStatisticsDto.getUpper20()+convertToNum+1);
                }
            }

        }
        PeopleStatistics statistics=convertDtoToEntity(peopleStatisticsDto,restaurantId);
        peopleStatisticsRepository.save(statistics);
    }

    private PeopleStatisticsDto convertEntityToDto(PeopleStatistics entity){
        return PeopleStatisticsDto.builder()
                .lower5(entity.getLower5())
                .lower10(entity.getLower10())
                .lower20(entity.getLower20())
                .upper20(entity.getUpper20())
                .build();
    }

    private PeopleStatistics convertDtoToEntity(PeopleStatisticsDto dto,Long restaurantId){
        return PeopleStatistics.builder()
                .lower5(dto.getLower5())
                .lower10(dto.getLower10())
                .lower20(dto.getLower20())
                .upper20(dto.getUpper20())
                .restaurantId(restaurantId)
                .build();
    }


}
