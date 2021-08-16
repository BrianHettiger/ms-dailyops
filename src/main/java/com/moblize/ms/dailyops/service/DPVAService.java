package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.mongo.PlannedDataDpva;
import com.moblize.ms.dailyops.domain.mongo.SurveyDataDpva;
import com.moblize.ms.dailyops.repository.mongo.client.PlannedDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.client.SurveyDataDPVARepository;
import com.moblize.ms.dailyops.service.dto.DPVAData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DPVAService {

    @Autowired
    private PlannedDataDPVARepository plannedDataDPVARepository;
    @Autowired
    private SurveyDataDPVARepository surveyDataDPVARepository;

    public SurveyDataDpva saveSurveyDataDpva(SurveyDataDpva surveyDataDpva) {
        SurveyDataDpva surveyDataDpvaDB = surveyDataDPVARepository.findFirstByUid(surveyDataDpva.getWellUid());
        if (null != surveyDataDpvaDB) {
            surveyDataDpvaDB.setWellStatus(surveyDataDpva.getWellStatus());
            surveyDataDpvaDB.setScaledSurveyData(surveyDataDpva.getScaledSurveyData());
            return surveyDataDPVARepository.save(surveyDataDpvaDB);
        } else {
            return surveyDataDPVARepository.save(surveyDataDpva);
        }
    }

    public PlannedDataDpva savePlannedDataDpva(PlannedDataDpva plannedDataDpva) {
        PlannedDataDpva plannedDataDpvaDB = plannedDataDPVARepository.findFirstByUid(plannedDataDpva.getWellUid());
        if (null != plannedDataDpvaDB) {
            plannedDataDpvaDB.setWellStatus(plannedDataDpva.getWellStatus());
            plannedDataDpvaDB.setScaledPlannedData(plannedDataDpva.getScaledPlannedData());
            return plannedDataDPVARepository.save(plannedDataDpvaDB);
        } else {
            return plannedDataDPVARepository.save(plannedDataDpva);
        }
    }

    public List<DPVAData> getDPVAData(List<String> wellUids) {
        List<DPVAData> result = new ArrayList<>();
        List<SurveyDataDpva> surveyList = surveyDataDPVARepository.findByUidIn(wellUids);
        Map<String, SurveyDataDpva> surveyMap = Collections.emptyMap();
        Map<String, PlannedDataDpva> plannedMap = Collections.emptyMap();
        if (surveyList != null) {
            surveyMap = surveyList.stream().collect(Collectors.toMap(SurveyDataDpva::getWellUid, Function.identity()));
        }
        List<PlannedDataDpva> plannedList = plannedDataDPVARepository.findByUidIn(wellUids);
        if (plannedList != null) {
            plannedMap = plannedList.stream().collect(Collectors.toMap(PlannedDataDpva::getWellUid, Function.identity()));
        }
        Map<String, PlannedDataDpva> finalPlannedMap = plannedMap;
        Map<String, SurveyDataDpva> finalSurveyMap = surveyMap;
        wellUids.forEach(well->{
            DPVAData dpvaData = new DPVAData();
            dpvaData.setWellUid(well);
            dpvaData.setPlannedData(finalPlannedMap.getOrDefault(well, new PlannedDataDpva()).getScaledPlannedData());
            dpvaData.setSurveyData(finalSurveyMap.getOrDefault(well, new SurveyDataDpva()).getScaledSurveyData());
            result.add(dpvaData);
        });
        return result;
    }
}
