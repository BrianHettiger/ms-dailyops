package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.PlannedDataDpva;
import com.moblize.ms.dailyops.domain.mongo.SurveyDataDpva;
import com.moblize.ms.dailyops.repository.mongo.client.PlannedDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.client.SurveyDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.DPVAData;
import com.moblize.ms.dailyops.service.dto.PlannedPerFeetDTO;
import com.moblize.ms.dailyops.service.dto.SurveyPerFeetDTO;
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
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private CacheService cacheService;

    public SurveyPerFeetDTO saveSurveyDataDpva(SurveyPerFeetDTO surveyPerFeetDTO) {
        try {
            if (surveyPerFeetDTO.getWellStatus().equalsIgnoreCase("active")) {
                cacheService.getPerFeetSurveyDataCache().put(surveyPerFeetDTO.getWellUid(), surveyPerFeetDTO);
            } else {
                SurveyDataDpva surveyDataDpvaDB = surveyDataDPVARepository.findFirstByWellUid(surveyPerFeetDTO.getWellUid());
                if (null != surveyDataDpvaDB) {
                    surveyDataDpvaDB.setWellStatus(surveyPerFeetDTO.getWellStatus());
                    surveyDataDpvaDB.setScaledSurveyData(surveyPerFeetDTO.getScaledSurveyData());
                    surveyDataDPVARepository.save(surveyDataDpvaDB);
                } else {
                    SurveyDataDpva surveyDataDpva = new SurveyDataDpva();
                    surveyDataDpva.setWellUid(surveyPerFeetDTO.getWellUid());
                    surveyDataDpva.setWellStatus(surveyPerFeetDTO.getWellStatus());
                    surveyDataDpva.setCustomer(surveyPerFeetDTO.getCustomer());
                    surveyDataDpva.setScaledSurveyData(surveyPerFeetDTO.getScaledSurveyData());
                    surveyDataDPVARepository.save(surveyDataDpva);
                }
            }

        } catch (Exception e) {
            log.error("Error occur in saveSurveyDataDpva ", e);
        }
        return surveyPerFeetDTO;
    }

    public PlannedPerFeetDTO savePlannedDataDpva(PlannedPerFeetDTO plannedPerFeetDTO) {
        try {

            if (plannedPerFeetDTO.getWellStatus().equalsIgnoreCase("active")) {
                cacheService.getPerFeetPlanDataCache().put(plannedPerFeetDTO.getWellUid(), plannedPerFeetDTO);
            } else {
                PlannedDataDpva plannedDataDpvaDB = plannedDataDPVARepository.findFirstByWellUid(plannedPerFeetDTO.getWellUid());
                if (null != plannedDataDpvaDB) {
                    plannedDataDpvaDB.setWellStatus(plannedPerFeetDTO.getWellStatus());
                    plannedDataDpvaDB.setScaledPlannedData(plannedPerFeetDTO.getScaledPlannedData());
                    plannedDataDPVARepository.save(plannedDataDpvaDB);
                } else {
                    plannedDataDpvaDB = new PlannedDataDpva();
                    plannedDataDpvaDB.setWellUid(plannedPerFeetDTO.getWellUid());
                    plannedDataDpvaDB.setWellStatus(plannedPerFeetDTO.getWellStatus());
                    plannedDataDpvaDB.setCustomer(plannedPerFeetDTO.getCustomer());
                    plannedDataDpvaDB.setScaledPlannedData(plannedPerFeetDTO.getScaledPlannedData());
                    plannedDataDPVARepository.save(plannedDataDpvaDB);
                }
            }
        } catch (Exception e) {
            log.error("Error occur in savePlannedDataDpva ", e);
        }
        return plannedPerFeetDTO;
    }

    public List<DPVAData> getDPVAData(List<String> wellUids) {
        List<DPVAData> result = new ArrayList<>();

        List<MongoWell> activeMongoWellList = mongoWellRepository.findAllByUidInAndStatusWell(wellUids, "active");
        List<MongoWell> completedMongoWellList = mongoWellRepository.findAllByUidInAndStatusWellNotContains(wellUids, "active");
        List<String> completedWellUID = completedMongoWellList.stream().map(mongoWell -> mongoWell.getUid()).collect(Collectors.toList());

        if (completedWellUID != null && !completedWellUID.isEmpty()) {
            List<SurveyDataDpva> surveyList = surveyDataDPVARepository.findByWellUidIn(completedWellUID);
            Map<String, SurveyDataDpva> surveyMap = Collections.emptyMap();
            Map<String, PlannedDataDpva> plannedMap = Collections.emptyMap();
            if (surveyList != null) {
                surveyMap = surveyList.stream().collect(Collectors.toMap(SurveyDataDpva::getWellUid, Function.identity()));
            }
            List<PlannedDataDpva> plannedList = plannedDataDPVARepository.findByWellUidIn(completedWellUID);
            if (plannedList != null) {
                plannedMap = plannedList.stream().collect(Collectors.toMap(PlannedDataDpva::getWellUid, Function.identity()));
            }
            Map<String, PlannedDataDpva> finalPlannedMap = plannedMap;
            Map<String, SurveyDataDpva> finalSurveyMap = surveyMap;
            completedWellUID.forEach(well -> {
                DPVAData dpvaData = new DPVAData();
                dpvaData.setWellUid(well);
                dpvaData.setPlannedData(finalPlannedMap.getOrDefault(well, new PlannedDataDpva()).getScaledPlannedData());
                dpvaData.setSurveyData(finalSurveyMap.getOrDefault(well, new SurveyDataDpva()).getScaledSurveyData());
                result.add(dpvaData);
            });
        }
        if( null != activeMongoWellList && !activeMongoWellList.isEmpty()){

            activeMongoWellList.forEach(well -> {
                DPVAData dpvaData = new DPVAData();
                dpvaData.setWellUid(well.getUid());
                dpvaData.setPlannedData(cacheService.getPerFeetPlanDataCache().getOrDefault(well.getUid(), new PlannedPerFeetDTO()).getScaledPlannedData());
                dpvaData.setSurveyData(cacheService.getPerFeetSurveyDataCache().getOrDefault(well.getUid(), new SurveyPerFeetDTO()).getScaledSurveyData());
                result.add(dpvaData);
            });
        }
        return result;
    }
}
