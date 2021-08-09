package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    RestClientService restClientService;

    public void notifyDPVAJob(TargetWindowDPVA targetWindow, String wellStatus) {
        List<SurveyRecord> surveyData = null;
        List<WellPlan> planData = null;
        String wellUid = targetWindow.getUid();
        if (targetWindow.getIsEnable()) {
            surveyData = getSurveyRecords(wellUid, wellStatus);
        } else {
            planData = getPlanRecords(wellUid, wellStatus);
        }

        sendData(targetWindow, surveyData, planData, "targetWindow");
    }

    public void notifyDPVAJobForSurveyData(String wellUid, String wellStatus) {
        List<SurveyRecord>  surveyData = getSurveyRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, surveyData, null, "survey");
    }

    public void notifyDPVAJobForPlanData(String wellUid, String wellStatus) {
        List<WellPlan>  planData = getPlanRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, null, planData, "plan");
    }

    private List<WellPlan> getPlanRecords(String wellUid, String wellStatus) {
        List<WellPlan> planData;
        if (cacheService.getPlanDataCache().containsKey(wellUid) && wellStatus.equalsIgnoreCase("active")) {
            WellPlanCacheDTO  wellPlanCacheDTO  = cacheService.getPlanDataCache().get(wellUid);
            planData = wellPlanCacheDTO.getWellPlanList();
        } else {
            planData = alarmDetailClient.getPlanData(wellUid, wellStatus);
        }
        return planData;
    }

    private List<SurveyRecord> getSurveyRecords(String wellUid, String wellStatus) {
        List<SurveyRecord> surveyData;
        if (cacheService.getSurveyDataCache().containsKey(wellUid)&& wellStatus.equalsIgnoreCase("active")) {
            SurveyCacheDTO surveyCacheDTO = cacheService.getSurveyDataCache().get(wellUid);
            surveyData = surveyCacheDTO.getSurveyRecordList();
        } else {
            surveyData = alarmDetailClient.getSurveyData(wellUid, wellStatus);
        }
        return surveyData;
    }

    public void sendData(TargetWindowDPVA targetWindow, List<SurveyRecord> surveyData, List<WellPlan> plannedData, String dataUpdate) {
        ProcessPerFeetRequestDTO processPerFeetRequestDTO = new ProcessPerFeetRequestDTO(targetWindow,surveyData,plannedData,dataUpdate);
        restClientService.processPerFeetData(processPerFeetRequestDTO);
    }
}
