package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.DailyOpsLoadConfig;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.repository.mongo.client.DPVALoadConfigRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import com.moblize.ms.dailyops.service.dto.ProcessPerFeetRequestDTO;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import com.moblize.ms.dailyops.service.dto.WellPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@EnableAsync
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
    @Autowired
    private DiscoveryClient discoveryClient;



    @Value("${CODE}")
    private String code;

    @Async
    public void loadDPVAData(String customer,DailyOpsLoadConfig dailyOpsLoadConfig) {
        try {
            if (dailyOpsLoadConfig != null && !dailyOpsLoadConfig.getIsDPVACalculated()) {
                log.warn("Going to recalculate DPVA data...");
                cacheService.getTortuosityDataCache().clear();
                cacheService.getPerFeetTargetWindowDataCache().clear();
                cacheService.getPerFeetSurveyDataCache().clear();
                cacheService.getPerFeetPlanDataCache().clear();
                Thread.sleep(1000*120); // Process will be start after 2 min

                while(!retryKPIDashboardService() || !retryAlarmDetailService()) {
                    Thread.sleep(60000L);
                }

                mongoWellRepository.findAllByCustomer(customer).forEach(well -> {
                    notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(well.getUid()), well.getStatusWell());
                });
                dailyOpsLoadConfig.setIsDPVACalculated(true);
                dpvaLoadConfigRepository.save(dailyOpsLoadConfig);
                log.warn("DPVA data calculated");
            }
        } catch (Exception e) {
            log.error("Error occurred in loadDPVAData ", e);
        }

    }

    private boolean retryKPIDashboardService()  {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("KPIDASHBOARD");
        if (serviceInstanceList == null || serviceInstanceList.isEmpty()) {
            log.error("KPIDASHBOARD service not found. Will retry after 1 minute.");
            return false;
        }
        return true;
    }

    private boolean retryAlarmDetailService()  {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("ALARMDETAIL");
        if (serviceInstanceList == null || serviceInstanceList.isEmpty()) {
            log.error("ALARMDETAIL service not found. Will retry after 1 minute.");
            return false;
        }
        return true;
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
        processDPVADataForWell(targetWindow, wellStatus);
    }

    public void notifyDPVAJobForSaveTargetWindow(TargetWindowDPVA targetWindow, String wellStatus) {
        processDPVADataForWell(targetWindow, wellStatus);
    }

    private void processDPVADataForWell(TargetWindowDPVA targetWindow, String wellStatus) {
        try {
            List<SurveyRecord> surveyData = null;
            List<WellPlan> planData = null;
            String wellUid = targetWindow.getUid();
            surveyData = getSurveyRecords(wellUid, wellStatus);
            planData = getPlanRecords(wellUid, wellStatus);

            sendData(targetWindow, surveyData, planData, "targetWindow", wellUid, wellStatus);
        } catch (Exception e) {
            log.error("Error occur in notifyDPVAJob for wellUID: {}", targetWindow.getUid(), e);
        }
    }

    public void notifyDPVAJobForSurveyData(String wellUid, String wellStatus) {
        wellStatus = getWellStatus(wellUid, wellStatus);
        List<SurveyRecord> surveyData = getSurveyRecords(wellUid, wellStatus);
        List<WellPlan> planData = getPlanRecords(wellUid, wellStatus);
        TargetWindowDPVA targetWindow = targetWindowDPVAService.getTargetWindowDetail(wellUid);
        sendData(targetWindow, surveyData, planData, "survey", wellUid, wellStatus);
    }

    private String getWellStatus(String wellUid, String wellStatus) {
        try {
            MongoWell mongoWell = mongoWellRepository.findByUid(wellUid);
            wellStatus = mongoWell.getStatusWell();
        } catch (Exception e) {
            log.error("Error occur for well UID {} to get updated status", wellUid, e);
        }
        return wellStatus;
    }

    public void notifyDPVAJobForPlanData(String wellUid, String wellStatus) {
        List<WellPlan> planData = getPlanRecords(wellUid, wellStatus);
        wellStatus = getWellStatus(wellUid, wellStatus);
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
        ProcessPerFeetRequestDTO processPerFeetRequestDTO = new ProcessPerFeetRequestDTO(targetWindow, surveyData, plannedData, dataUpdate, wellUid, code, wellStatus, getLateralLength(wellUid));
        restClientService.processPerFeetData(processPerFeetRequestDTO);
    }

    public Float getLateralLength(String wellUid) {
        try {
            final List<HoleSection> holeSections  = kpiDashboardClient.getHoleSections(wellUid);
            final Optional<Float> lateralDepth = holeSections.stream()
                .filter(holeSection -> holeSection.getSection().name().equalsIgnoreCase("lateral"))
                .map(HoleSection::getFromDepth)
                .findFirst();
            if(lateralDepth.isPresent()){
                return lateralDepth.get();
            }
        } catch (Exception e) {
            log.error("Error while retrieving Lateral Hole section for Well UID {} ", wellUid, e);
        }
        return null;
    }

    public void resetDPVAWell(String wellUid) {
        try {
            MongoWell mongoWell = mongoWellRepository.findByUid(wellUid);

            notifyDPVAJob(targetWindowDPVAService.getTargetWindowDetail(mongoWell.getUid()), mongoWell.getStatusWell());
        } catch (Exception e) {
            log.error("Error in resetDPVAWell for well uid: {}",wellUid, e);
        }
    }

    public void resetAllDPVAWell() {
        try {
            DailyOpsLoadConfig dailyOpsLoadConfig = dpvaLoadConfigRepository.findFirstByCustomer(code);
            if (dailyOpsLoadConfig == null) {
                dailyOpsLoadConfig = new DailyOpsLoadConfig();
            }
            dailyOpsLoadConfig.setCustomer(code);
            dailyOpsLoadConfig.setIsDPVACalculated(false);
            dailyOpsLoadConfig.setIsPerformanceMapCalculated(true);
            dailyOpsLoadConfig =   dpvaLoadConfigRepository.save(dailyOpsLoadConfig);
            loadDPVAData(code, dailyOpsLoadConfig);
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
            log.error("Error occur in dpvaWellCompletedNotification service for well uid {}", wellUid, e);
        }
    }
}
