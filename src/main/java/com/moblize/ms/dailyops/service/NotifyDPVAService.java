package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.DailyOpsLoadConfig;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.repository.mongo.client.DPVALoadConfigRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NotifyDPVAService {
    @Autowired
    @Lazy
    private CacheService cacheService;
    @Autowired
    private AlarmDetailClient alarmDetailClient;
    @Autowired
    private TargetWindowDPVAService targetWindowDPVAService;
    @Autowired
    private RestClientService restClientService;
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private DPVALoadConfigRepository dpvaLoadConfigRepository;
    @Autowired
    private KpiDashboardClient kpiDashboardClient;


    @Value("${CODE}")
    private String code;

    public void loadDPVAData(String customer,DailyOpsLoadConfig dailyOpsLoadConfig) {
        try {
            if (dailyOpsLoadConfig != null && !dailyOpsLoadConfig.getIsDPVACalculated()) {
                mongoWellRepository.findAllByCustomer(customer).forEach(well -> {
                    notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(well.getUid()), well.getStatusWell());
                });
                dailyOpsLoadConfig.setIsDPVACalculated(true);
                dpvaLoadConfigRepository.save(dailyOpsLoadConfig);
            }
        } catch (Exception e) {
            log.error("Error occur in loadDPVAData ", e);
        }
    }

    public DailyOpsLoadConfig getDailyOpsLoadConfig(String customer) {
        DailyOpsLoadConfig dailyOpsLoadConfig = dpvaLoadConfigRepository.findFirstByCustomer(customer);
        if(dailyOpsLoadConfig == null){
            dailyOpsLoadConfig = new DailyOpsLoadConfig();
            dailyOpsLoadConfig.setCustomer(customer);
            dailyOpsLoadConfig.setIsDPVACalculated(false);
            dailyOpsLoadConfig.setIsPerformanceMapCalculated(true);
            dailyOpsLoadConfig = dpvaLoadConfigRepository.save(dailyOpsLoadConfig);
        }
        return dailyOpsLoadConfig;
    }

    public void notifyDPVAJob(TargetWindowDPVA targetWindow, String wellStatus) {
        try {
            List<SurveyRecord> surveyData = null;
            List<WellPlan> planData = null;
            String wellUid = targetWindow.getUid();
            surveyData = getSurveyRecords(wellUid, wellStatus);
            planData = getPlanRecords(wellUid, wellStatus);

            sendData(targetWindow, surveyData, planData, "targetWindow", wellUid, wellStatus);
        } catch (Exception e) {
            log.error("Error occur in notifyDPVAJob ", e);
        }
    }

    public void notifyDPVAJobForSurveyData(String wellUid, String wellStatus) {
        List<SurveyRecord> surveyData = getSurveyRecords(wellUid, wellStatus);
        List<WellPlan> planData = getPlanRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, surveyData, planData, "survey", wellUid, wellStatus);
    }

    public void notifyDPVAJobForPlanData(String wellUid, String wellStatus) {
        List<WellPlan> planData = getPlanRecords(wellUid, wellStatus);
        List<SurveyRecord> surveyData = getSurveyRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, surveyData, planData, "plan", wellUid, wellStatus);
    }

    private List<WellPlan> getPlanRecords(String wellUid, String wellStatus) {
        return alarmDetailClient.getPlanData(wellUid, wellStatus);
    }

    public List<SurveyRecord> getSurveyRecords(String wellUid, String wellStatus) {
        return alarmDetailClient.getSurveyData(wellUid, wellStatus);
    }

    public void sendData(TargetWindowDPVA targetWindow, List<SurveyRecord> surveyData, List<WellPlan> plannedData, String dataUpdate, String wellUid, String wellStatus) {
        if (plannedData != null && !plannedData.isEmpty()) {
            ProcessPerFeetRequestDTO processPerFeetRequestDTO = new ProcessPerFeetRequestDTO(targetWindow, surveyData, plannedData, dataUpdate, wellUid, code, wellStatus,getLateralLength(wellUid));
            restClientService.processPerFeetData(processPerFeetRequestDTO);
        }
    }

    public Float getLateralLength(String wellUid) {
        try {
            Optional<Float> lateralDepth = kpiDashboardClient.getHoleSections(wellUid).stream().filter(holeSection -> holeSection.getSection().name().equalsIgnoreCase("lateral")).map(holeSection -> holeSection.getFromDepth()).findFirst();
            if(lateralDepth.isPresent()){
                return lateralDepth.get();
            }
        } catch (Exception e) {
            log.error("Error in getLateralLength ", e);
        }
        return null;
    }

    public void resetDPVAWell(String wellUid) {
        try {
            MongoWell mongoWell = mongoWellRepository.findByUid(wellUid);

            notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(mongoWell.getUid()), mongoWell.getStatusWell());
        } catch (Exception e) {
            log.error("Error in resetDPVAWell ", e);
        }
    }

    public void resetAllDPVAWell(String customer) {
        try {
            DailyOpsLoadConfig dailyOpsLoadConfig = dpvaLoadConfigRepository.findFirstByCustomer(customer);
            if (dailyOpsLoadConfig == null) {
                dailyOpsLoadConfig = new DailyOpsLoadConfig();
            }
            dailyOpsLoadConfig.setCustomer(customer);
            dailyOpsLoadConfig.setIsDPVACalculated(false);
            dailyOpsLoadConfig.setIsPerformanceMapCalculated(true);
            dailyOpsLoadConfig =   dpvaLoadConfigRepository.save(dailyOpsLoadConfig);
            loadDPVAData(customer, dailyOpsLoadConfig);
        } catch (Exception e) {
            log.error("Error occur in resetAllDPVAWell ", e);
        }
    }

    public void dpvaWellCompletedNotification(String wellUid) {

        try {
            MongoWell mongoWell = mongoWellRepository.findByUid(wellUid);
            if (!mongoWell.getStatusWell().equalsIgnoreCase("active")) {
                //cacheService.getSurveyDataCache().removeAsync(wellUid);
                cacheService.getPerFeetSurveyDataCache().removeAsync(wellUid);
                //cacheService.getPlanDataCache().removeAsync(wellUid);
                cacheService.getPerFeetPlanDataCache().removeAsync(wellUid);
                cacheService.getPerFeetTargetWindowDataCache().removeAsync(wellUid);
                notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(mongoWell.getUid()), mongoWell.getStatusWell());
            }
        } catch (Exception e) {
            log.error("Error occur in dpvaWellCompletedNotification service ", e);
        }
    }
}
