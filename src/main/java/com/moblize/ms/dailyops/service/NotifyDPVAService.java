package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.DPVALoadConfig;
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

    @Value("${CODE}")
    private String code;

    public void loadDPVAData(String customer) {
        try {
            DPVALoadConfig dpvaLoadConfig = dpvaLoadConfigRepository.findFirstByCustomer(customer);
            if (dpvaLoadConfig == null || (dpvaLoadConfig != null && !dpvaLoadConfig.getIsDataCalculated())) {
                mongoWellRepository.findAllByCustomer(customer).forEach(well -> {
                    notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(well.getUid()), well.getStatusWell());
                });
                DPVALoadConfig dpvaLoadConfigNew = new DPVALoadConfig();
                dpvaLoadConfigNew.setCustomer(customer);
                dpvaLoadConfigNew.setIsDataCalculated(true);
                dpvaLoadConfigRepository.save(dpvaLoadConfigNew);
            }
        } catch (Exception e) {
            log.error("Error occur in loadDPVAData ", e);
        }
    }

    public void notifyDPVAJob(TargetWindowDPVA targetWindow, String wellStatus) {
        try {
            List<SurveyRecord> surveyData = null;
            List<WellPlan> planData = null;
            String wellUid = targetWindow.getUid();
            surveyData = getSurveyRecords(wellUid, wellStatus);
            planData = getPlanRecords(wellUid, wellStatus);

            sendData(targetWindow, surveyData, planData, "targetWindow", wellUid, wellStatus );
        } catch (Exception e) {
            log.error("Error occur in notifyDPVAJob ", e);
        }
    }

    public void notifyDPVAJobForSurveyData(String wellUid, String wellStatus) {
        List<SurveyRecord> surveyData = getSurveyRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, surveyData, null, "survey", wellUid, wellStatus);
    }

    public void notifyDPVAJobForPlanData(String wellUid, String wellStatus) {
        List<WellPlan> planData = getPlanRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, null, planData, "plan", wellUid, wellStatus);
    }

    private List<WellPlan> getPlanRecords(String wellUid, String wellStatus) {
        List<WellPlan> planData;
        if (cacheService.getPlanDataCache().containsKey(wellUid) && wellStatus.equalsIgnoreCase("active")) {
            WellPlanCacheDTO wellPlanCacheDTO = cacheService.getPlanDataCache().get(wellUid);
            planData = wellPlanCacheDTO.getWellPlanList();
        } else {
            planData = alarmDetailClient.getPlanData(wellUid, wellStatus);
        }
        return planData;
    }

    private List<SurveyRecord> getSurveyRecords(String wellUid, String wellStatus) {
        List<SurveyRecord> surveyData;
        if (cacheService.getSurveyDataCache().containsKey(wellUid) && wellStatus.equalsIgnoreCase("active")) {
            SurveyCacheDTO surveyCacheDTO = cacheService.getSurveyDataCache().get(wellUid);
            surveyData = surveyCacheDTO.getSurveyRecordList();
        } else {
            surveyData = alarmDetailClient.getSurveyData(wellUid, wellStatus);
        }
        return surveyData;
    }

    public void sendData(TargetWindowDPVA targetWindow, List<SurveyRecord> surveyData, List<WellPlan> plannedData, String dataUpdate, String wellUid, String wellStatus) {
        ProcessPerFeetRequestDTO processPerFeetRequestDTO = new ProcessPerFeetRequestDTO(targetWindow, surveyData, plannedData, dataUpdate, wellUid, wellStatus, code);
        restClientService.processPerFeetData(processPerFeetRequestDTO);
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
            DPVALoadConfig dpvaLoadConfig = dpvaLoadConfigRepository.findFirstByCustomer(customer);
            if (dpvaLoadConfig == null) {
                dpvaLoadConfig = new DPVALoadConfig();
            }
            dpvaLoadConfig.setCustomer(customer);
            dpvaLoadConfig.setIsDataCalculated(false);
            dpvaLoadConfigRepository.save(dpvaLoadConfig);
            loadDPVAData(customer);
        } catch (Exception e) {
            log.error("Error occur in resetAllDPVAWell ", e);
        }
    }

    public void dpvaWellCompletedNotification(String wellUid) {

        try {
            MongoWell mongoWell = mongoWellRepository.findByUid(wellUid);
            if (!mongoWell.getStatusWell().equalsIgnoreCase("active")) {
                cacheService.getSurveyDataCache().removeAsync(wellUid);
                cacheService.getPerFeetSurveyDataCache().removeAsync(wellUid);
                cacheService.getPlanDataCache().removeAsync(wellUid);
                cacheService.getPerFeetPlanDataCache().removeAsync(wellUid);
                cacheService.getPerFeetTargetWindowDataCache().removeAsync(wellUid);
                notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(mongoWell.getUid()), mongoWell.getStatusWell());
            }
        } catch (Exception e) {
            log.error("Error occur in dpvaWellCompletedNotification service ", e);
        }
    }
}
