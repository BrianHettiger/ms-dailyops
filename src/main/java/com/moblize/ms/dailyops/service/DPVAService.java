package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.ScaledSurveyData;
import com.moblize.ms.dailyops.domain.mongo.PlannedDataDpva;
import com.moblize.ms.dailyops.domain.mongo.SurveyDataDpva;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowPerFootDPVA;
import com.moblize.ms.dailyops.dto.DPVARequestDTO;
import com.moblize.ms.dailyops.repository.mongo.client.PlannedDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.client.SurveyDataDPVARepository;
import com.moblize.ms.dailyops.repository.mongo.client.TargetWindowPerFootRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DPVAService {

    @Autowired
    private PlannedDataDPVARepository plannedDataDPVARepository;
    @Autowired
    private SurveyDataDPVARepository surveyDataDPVARepository;
    @Autowired
    private TargetWindowPerFootRepository targetWindowPerFootRepository;
    @Autowired
    private TargetWindowDPVAService targetWindowDPVAService;
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private NotifyDPVAService notifyDPVAService;

    private final static String ACTIVE_STATUS = "active";
    private final static String COMPLETED_STATUS = "Completed";

    public SurveyPerFeetDTO saveSurveyDataDpva(SurveyPerFeetDTO surveyPerFeetDTO) {
        try {
            if (surveyPerFeetDTO.getWellStatus().equalsIgnoreCase("active")) {
                cacheService.getPerFeetSurveyDataCache().put(surveyPerFeetDTO.getWellUid(), surveyPerFeetDTO);
            } else {
                SurveyDataDpva surveyDataDpvaDB = surveyDataDPVARepository.findFirstByWellUid(surveyPerFeetDTO.getWellUid());
                if (null != surveyDataDpvaDB) {
                    surveyDataDpvaDB.setWellStatus(surveyPerFeetDTO.getWellStatus());
                    surveyDataDpvaDB.setScaledSurveyData(surveyPerFeetDTO.getScaledSurveyData());
                    surveyDataDpvaDB.setPvInPercentage(surveyPerFeetDTO.getPvInPercentage());
                    surveyDataDpvaDB.setSvInPercentage(surveyPerFeetDTO.getSvInPercentage());
                    surveyDataDPVARepository.save(surveyDataDpvaDB);
                } else {
                    SurveyDataDpva surveyDataDpva = new SurveyDataDpva();
                    surveyDataDpva.setWellUid(surveyPerFeetDTO.getWellUid());
                    surveyDataDpva.setWellStatus(surveyPerFeetDTO.getWellStatus());
                    surveyDataDpva.setCustomer(surveyPerFeetDTO.getCustomer());
                    surveyDataDpva.setScaledSurveyData(surveyPerFeetDTO.getScaledSurveyData());
                    surveyDataDpva.setPvInPercentage(surveyPerFeetDTO.getPvInPercentage());
                    surveyDataDpva.setSvInPercentage(surveyPerFeetDTO.getSvInPercentage());
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
                } else {
                    plannedDataDpvaDB = new PlannedDataDpva();
                    plannedDataDpvaDB.setWellUid(plannedPerFeetDTO.getWellUid());
                    plannedDataDpvaDB.setWellStatus(plannedPerFeetDTO.getWellStatus());
                    plannedDataDpvaDB.setCustomer(plannedPerFeetDTO.getCustomer());
                    plannedDataDpvaDB.setScaledPlannedData(plannedPerFeetDTO.getScaledPlannedData());
                }
                plannedDataDPVARepository.save(plannedDataDpvaDB);
            }
        } catch (Exception e) {
            log.error("Error occur in savePlannedDataDpva ", e);
        }
        return plannedPerFeetDTO;
    }


    public TargetWindowPerFootDTO savePerFootTargetWindowDpva(TargetWindowPerFootDTO targetWindowPerFootDTO) {
        try {
            if (targetWindowPerFootDTO.getWellStatus().equalsIgnoreCase("active")) {
                targetWindowPerFootDTO.setProtoData();
                cacheService.getPerFeetTargetWindowDataCache().put(targetWindowPerFootDTO.getWellUid(), targetWindowPerFootDTO);
            } else {
                TargetWindowPerFootDPVA targetWindowPerFootDPVADB = targetWindowPerFootRepository.findFirstByWellUid(targetWindowPerFootDTO.getWellUid());
                if (null == targetWindowPerFootDPVADB) {
                    targetWindowPerFootDPVADB = new TargetWindowPerFootDPVA();
                }
                targetWindowPerFootDPVADB.setWellStatus(targetWindowPerFootDTO.getWellStatus());
                targetWindowPerFootDPVADB.setCustomer(targetWindowPerFootDTO.getCustomer());
                targetWindowPerFootDPVADB.setWellUid(targetWindowPerFootDTO.getWellUid());
                targetWindowPerFootDPVADB.setBasic(targetWindowPerFootDTO.getBasic());
                targetWindowPerFootDPVADB.setAdvance(targetWindowPerFootDTO.getAdvance());
                targetWindowPerFootDPVADB.setPvFirstLine(targetWindowPerFootDTO.getPvFirstLine());
                targetWindowPerFootDPVADB.setPvCenterLine(targetWindowPerFootDTO.getPvCenterLine());
                targetWindowPerFootDPVADB.setPvLastLine(targetWindowPerFootDTO.getPvLastLine());
                targetWindowPerFootDPVADB.setPvSideLine(targetWindowPerFootDTO.getPvSideLine());
                targetWindowPerFootDPVADB.setSvFirstLine(targetWindowPerFootDTO.getSvFirstLine());
                targetWindowPerFootDPVADB.setSvCenterLine(targetWindowPerFootDTO.getSvCenterLine());
                targetWindowPerFootDPVADB.setSvLastLine(targetWindowPerFootDTO.getSvLastLine());
                targetWindowPerFootDPVADB.setSvSideLine(targetWindowPerFootDTO.getSvSideLine());
                targetWindowPerFootDPVADB.setSvIntersections(targetWindowPerFootDTO.getSvIntersections());
                targetWindowPerFootDPVADB.setPvIntersections(targetWindowPerFootDTO.getPvIntersections());
                targetWindowPerFootRepository.save(targetWindowPerFootDPVADB);
            }
        } catch (Exception e) {
            log.error("Error occur in savePerFootTargetWindowDpva ", e);
        }
        return targetWindowPerFootDTO;
    }

    public DPVAResult getDPVAData(DPVARequestDTO dpvaRequestDTO) {

        DPVAResult dpvaResult = new DPVAResult();
        List<String> wellUids = new ArrayList<>();
        wellUids.add(dpvaRequestDTO.getPrimaryWell());
        wellUids.addAll(dpvaRequestDTO.getOffsetWells());

        MongoWell primaryMongoWell = mongoWellRepository.findByUid(dpvaRequestDTO.getPrimaryWell());
        List<MongoWell> offsetWells = mongoWellRepository.findAllByUidIn(dpvaRequestDTO.getOffsetWells());
        TargetWindowDPVA targetWindowDPVA = targetWindowDPVAService.getTargetWindowDetail(dpvaRequestDTO.getPrimaryWell());

        offsetWells.forEach(well -> {
            DPVAData dpvaData = new DPVAData();
            dpvaData.setWellUid(well.getUid());
            if (well.getStatusWell().equalsIgnoreCase(ACTIVE_STATUS)) {
                SurveyPerFeetDTO surveyPerFeetCache = cacheService.getPerFeetSurveyDataCache().getOrDefault(dpvaRequestDTO.getPrimaryWell(), new SurveyPerFeetDTO());
                dpvaData.setSurveyData(surveyPerFeetCache.getScaledSurveyData());
            } else {
                SurveyDataDpva surveyDataDpva = surveyDataDPVARepository.findFirstByWellUid(dpvaRequestDTO.getPrimaryWell());
                if(surveyDataDpva == null){
                    surveyDataDpva = new SurveyDataDpva();
                }
                dpvaData.setSurveyData( surveyDataDpva.getScaledSurveyData());
            }
            dpvaResult.getOffsetWellsDPVAData().add(dpvaData);

        });


        DPVAData dpvaData = new DPVAData();
        if (!primaryMongoWell.getStatusWell().equalsIgnoreCase(ACTIVE_STATUS)) {
            SurveyDataDpva surveyDataDpva = surveyDataDPVARepository.findFirstByWellUid(dpvaRequestDTO.getPrimaryWell());
            PlannedDataDpva plannedDataDpva = plannedDataDPVARepository.findFirstByWellUid(dpvaRequestDTO.getPrimaryWell());
            TargetWindowPerFootDPVA targetWindowPerFootDPVA = targetWindowPerFootRepository.findFirstByWellUid(dpvaRequestDTO.getPrimaryWell());
            dpvaData.setWellUid(dpvaRequestDTO.getPrimaryWell());
            dpvaData.setPlannedData(plannedDataDpva.getScaledPlannedData());
            dpvaData.setSurveyData(surveyDataDpva.getScaledSurveyData());
            if(targetWindowDPVA.getIsEnable()) {
                SectionPlanView sectionView = new SectionPlanView();
                sectionView.setFootagePercentage(surveyDataDpva.getSvInPercentage());
                TargetWindowsData targetWindowsData = new TargetWindowsData();
                targetWindowsData.setFirstLine(targetWindowPerFootDPVA.getSvFirstLine());
                targetWindowsData.setCenterLine(targetWindowPerFootDPVA.getSvCenterLine());
                targetWindowsData.setLastLine(targetWindowPerFootDPVA.getSvLastLine());
                targetWindowsData.setSideLines(targetWindowPerFootDPVA.getSvSideLine());
                targetWindowsData.setIntersections(targetWindowPerFootDPVA.getSvIntersections());
                sectionView.setTargetWindowsData(targetWindowsData);
                dpvaData.setSectionView(sectionView);

                SectionPlanView planView = new SectionPlanView();
                planView.setFootagePercentage(surveyDataDpva.getSvInPercentage());
                TargetWindowsData planViewData = new TargetWindowsData();
                planViewData.setFirstLine(targetWindowPerFootDPVA.getSvFirstLine());
                planViewData.setCenterLine(targetWindowPerFootDPVA.getSvCenterLine());
                planViewData.setLastLine(targetWindowPerFootDPVA.getSvLastLine());
                planViewData.setSideLines(targetWindowPerFootDPVA.getSvSideLine());
                planViewData.setIntersections(targetWindowPerFootDPVA.getPvIntersections());
                planView.setTargetWindowsData(planViewData);
                dpvaData.setPlanView(planView);
            }

            dpvaData.setDonutDistance(donutDistance(dpvaData));

            List<SurveyRecord> surveyRecordList = notifyDPVAService.getSurveyRecords(dpvaRequestDTO.getPrimaryWell(), COMPLETED_STATUS);
            setDirectionalAngle(dpvaData, surveyRecordList);
            dpvaResult.setPrimaryWellDPVAData(dpvaData);

        }
        if (primaryMongoWell.getStatusWell().equalsIgnoreCase(ACTIVE_STATUS)) {
            dpvaData.setWellUid(dpvaRequestDTO.getPrimaryWell());
            dpvaData.setPlannedData(cacheService.getPerFeetPlanDataCache().getOrDefault(dpvaRequestDTO.getPrimaryWell(), new PlannedPerFeetDTO()).getScaledPlannedData());
            SurveyPerFeetDTO surveyPerFeetCache = cacheService.getPerFeetSurveyDataCache().getOrDefault(dpvaRequestDTO.getPrimaryWell(), new SurveyPerFeetDTO());
            dpvaData.setSurveyData(surveyPerFeetCache.getScaledSurveyData());
            TargetWindowPerFootDTO targetDTOCache = cacheService.getPerFeetTargetWindowDataCache().getOrDefault(dpvaRequestDTO.getPrimaryWell(), new TargetWindowPerFootDTO());
            targetDTOCache.setEntries();

            if(targetWindowDPVA.getIsEnable()) {
                SectionPlanView sectionView = new SectionPlanView();
                sectionView.setFootagePercentage(surveyPerFeetCache.getSvInPercentage());
                TargetWindowsData targetWindowsData = new TargetWindowsData();
                targetWindowsData.setFirstLine(targetDTOCache.getSvFirstLine());
                targetWindowsData.setCenterLine(targetDTOCache.getSvCenterLine());
                targetWindowsData.setLastLine(targetDTOCache.getSvLastLine());
                targetWindowsData.setSideLines(targetDTOCache.getSvSideLine());
                targetWindowsData.setIntersections(targetDTOCache.getSvIntersections());
                sectionView.setTargetWindowsData(targetWindowsData);
                dpvaData.setSectionView(sectionView);

                SectionPlanView planView = new SectionPlanView();
                planView.setFootagePercentage(surveyPerFeetCache.getPvInPercentage());
                TargetWindowsData planViewData = new TargetWindowsData();
                planViewData.setFirstLine(targetDTOCache.getSvFirstLine());
                planViewData.setCenterLine(targetDTOCache.getSvCenterLine());
                planViewData.setLastLine(targetDTOCache.getSvLastLine());
                planViewData.setSideLines(targetDTOCache.getSvSideLine());
                planViewData.setIntersections(targetDTOCache.getPvIntersections());
                planView.setTargetWindowsData(planViewData);
                dpvaData.setPlanView(planView);
            }
            dpvaData.setDonutDistance(donutDistance(dpvaData));

            List<SurveyRecord> surveyRecordList = notifyDPVAService.getSurveyRecords(dpvaRequestDTO.getPrimaryWell(), ACTIVE_STATUS);
            setDirectionalAngle(dpvaData, surveyRecordList);
        }
        dpvaResult.setPrimaryWellDPVAData(dpvaData);
        return dpvaResult;
    }

    private void setDirectionalAngle(DPVAData dpvaData, List<SurveyRecord> surveyRecordList) {
        if (surveyRecordList != null && !surveyRecordList.isEmpty()) {
            SurveyRecord surveyRecord = surveyRecordList.get(surveyRecordList.size() - 1);
            dpvaData.getSectionView().setAngleDirection((surveyRecord.getStartIncl() + surveyRecord.getIncl()) / 2);
            dpvaData.getPlanView().setAngleDirection((surveyRecord.getStartAzimuth() + surveyRecord.getAzimuth()) / 2);
        }
    }

    private DonutDistanceDTO donutDistance(DPVAData dpvaData) {
        DonutDistanceDTO donutDistanceDTO = new DonutDistanceDTO();

        Float lateralLength =  notifyDPVAService.getLateralLength(dpvaData.getWellUid());
        if (lateralLength != null) {
            Map<String, DistanceDTO> map = new HashMap<>();
            var wrapper = new Object() {
                double totalDistance = 0d;
            };
            Stack<Double> trajectoryStack = new Stack<>();
            dpvaData.getSurveyData().forEach(survey -> {
                if (survey.getMd() > lateralLength) {
                    Double previousMD = trajectoryStack.isEmpty() ? null : trajectoryStack.pop();
                    Double drilledDepth = previousMD != null ? survey.getMd() - previousMD : 0;

                    Double distance = survey.getSvDistance();
                    distance = distance == null ? 0d : distance;
                    donutProcess(map, distance, drilledDepth, "section");
                    wrapper.totalDistance += distance;

                    distance = survey.getPvDistance();
                    distance = distance == null ? 0d : distance;
                    donutProcess(map, distance, drilledDepth, "plan");
                    wrapper.totalDistance += distance;

                    trajectoryStack.push(survey.getMd());
                }
            });

            donutDistanceDTO.setData(map);
            if (dpvaData != null && dpvaData.getSurveyData() != null && !dpvaData.getSurveyData().isEmpty()) {
                donutDistanceDTO.setAvgDistance(wrapper.totalDistance / dpvaData.getSurveyData().size());
            }
        }
        return donutDistanceDTO;
    }

    private void donutProcess(Map<String, DistanceDTO> map, Double distance, Double drilledDepth, String viewType) {
        if (distance <= 10d) {
            calculateDistanceDonut(map, drilledDepth, "0-10", viewType);
        } else if (distance > 10d && distance <= 20d) {
            calculateDistanceDonut(map, drilledDepth, "10-20", viewType);
        } else if (distance > 20d && distance <= 30d) {
            calculateDistanceDonut(map, drilledDepth, "20-30", viewType);
        } else if (distance > 30d && distance <= 40d) {
            calculateDistanceDonut(map, drilledDepth, "30-40", viewType);
        } else if (distance > 40d && distance <= 50d) {
            calculateDistanceDonut(map, drilledDepth, "40-50", viewType);
        } else if (distance > 50d) {
            calculateDistanceDonut(map, drilledDepth, "+50", viewType);
        }
    }

    private void calculateDistanceDonut(Map<String, DistanceDTO> map, Double drilledDepth, String depthRange, String viewType) {
        DistanceDTO distanceDTO = map.getOrDefault(depthRange, new DistanceDTO());
        distanceDTO.increaseCount();
        if (viewType.equalsIgnoreCase("section")) {
            distanceDTO.setDrilledDepthSectionView(drilledDepth);
        } else {
            distanceDTO.setDrilledDepthPlanView(drilledDepth);
        }
        map.put(depthRange, distanceDTO);
    }

    @Getter
    @Setter
    public static class DistanceDTO {
        int count = 0;

        Double drilledDepthSectionView = 0.0d;

        Double drilledDepthPlanView = 0.0d;

        public void increaseCount() {
            this.count += 1;
        }

        public void setDrilledDepthSectionView(Double drilledDepthSectionView) {
            this.drilledDepthSectionView += drilledDepthSectionView;
        }

        public void setDrilledDepthPlanView(Double drilledDepthPlanView) {
            this.drilledDepthPlanView = drilledDepthPlanView;
        }
    }
}
