package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.repository.WellSurveyPlannedLatLongRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
@Component
public class WellsCoordinatesDao {

    @Autowired
    private WellSurveyPlannedLatLongRepository wellSurveyPlannedLatLongRepository;

    public List<WellSurveyPlannedLatLong> getWellCoordinates(){
        return wellSurveyPlannedLatLongRepository.findAll();
    }
}
