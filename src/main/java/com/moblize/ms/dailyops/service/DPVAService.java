package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.PlannedDataDpva;
import com.moblize.ms.dailyops.domain.mongo.SurveyDataDpva;
import com.moblize.ms.dailyops.repository.mongo.client.PlannedDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.client.SurveyDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.DPVAData;
import com.moblize.ms.dailyops.service.dto.DonutDistanceDTO;
import com.moblize.ms.dailyops.service.dto.PlannedPerFeetDTO;
import com.moblize.ms.dailyops.service.dto.SurveyPerFeetDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

                dpvaData.setDonutDistanceDTO(donutDistance(dpvaData));

                result.add(dpvaData);
            });
        }
        if( null != activeMongoWellList && !activeMongoWellList.isEmpty()){

            activeMongoWellList.forEach(well -> {
                DPVAData dpvaData = new DPVAData();
                dpvaData.setWellUid(well.getUid());
                dpvaData.setPlannedData(cacheService.getPerFeetPlanDataCache().getOrDefault(well.getUid(), new PlannedPerFeetDTO()).getScaledPlannedData());
                dpvaData.setSurveyData(cacheService.getPerFeetSurveyDataCache().getOrDefault(well.getUid(), new SurveyPerFeetDTO()).getScaledSurveyData());
                dpvaData.setDonutDistanceDTO(donutDistance(dpvaData));
                result.add(dpvaData);
            });
        }
        return result;
    }

    private DonutDistanceDTO donutDistance(DPVAData dpvaData) {
        DonutDistanceDTO donutDistanceDTO = new DonutDistanceDTO();

        Map<String, DistanceDTO> map = new HashMap<>();
        var wrapper = new Object(){ double totalDistance = 0d; };
        Stack<Double> trajectoryStack = new Stack<>();
        dpvaData.getSurveyData().forEach(survey->{
            Double distance = survey.getDistance();
            Double previousMD = trajectoryStack.isEmpty() ? null : trajectoryStack.pop();
            Double drilledDepth = previousMD != null ? survey.getMd() - previousMD : 0;
            if(distance<=10d){
                calculateDistanceDonut(map, drilledDepth, "0-10");
            } else  if(distance > 10d && distance <= 20d){
                calculateDistanceDonut(map, drilledDepth, "10-20");
            }else  if(distance > 20d && distance <= 30d){
                calculateDistanceDonut(map, drilledDepth, "20-30");
            }else  if(distance > 30d && distance <= 40d){
                calculateDistanceDonut(map, drilledDepth, "30-40");
            }else  if(distance > 40d && distance <= 50d){
                calculateDistanceDonut(map, drilledDepth, "40-50");
            }else  if(distance > 50d){
                calculateDistanceDonut(map, drilledDepth, "+50");
            }
            wrapper.totalDistance += distance;
            trajectoryStack.push(survey.getMd());
        });
        donutDistanceDTO.setData(map);
        if (dpvaData != null && dpvaData.getSurveyData() != null && !dpvaData.getSurveyData().isEmpty()) {
            donutDistanceDTO.setAvgDistance(wrapper.totalDistance / dpvaData.getSurveyData().size());
        }
        return donutDistanceDTO;
    }

    private void calculateDistanceDonut(Map<String, DistanceDTO> map, Double drilledDepth, String depthRange) {
        DistanceDTO distanceDTO = map.getOrDefault(depthRange, new DistanceDTO());
        distanceDTO.increaseCount();
        distanceDTO.setDrilledDepth(drilledDepth);
        map.put(depthRange, distanceDTO);
    }

    @Getter
    @Setter
    public static class DistanceDTO {
        int count = 0;
        Double drilledDepth = 0.0d;

        public void increaseCount() {
            this.count += 1;
        }

        public void setDrilledDepth(Double drilledDepth){
            this.drilledDepth += drilledDepth;
        }
    }
}
