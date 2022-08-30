package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import com.moblize.ms.dailyops.service.util.HoleSectionNotConfiguredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class AvgPerStandUtil {
    @Autowired
    private WitsmlLogService logService;
    @Autowired
    private MongoLogService mongoLogService;
    @Autowired
    private AlarmDetailClient alarmDetailClient;
    @Autowired
    private AvgPerStandCalculation avgPerStandCalculation;

    public List<SurveyRecord> getSurveyList(String wellUid) throws HoleSectionNotConfiguredException, SQLException {
        return alarmDetailClient.drillerDashBoardBuildAnalysis(wellUid);
    }

    public List<SurveyRecord> processAvgPerStandBasedOnSurvey(String wellUid, List<String> channels) throws Exception {

        List<SurveyRecord> surveyList = getSurveyList(wellUid);

        if (!surveyList.isEmpty()) {
            MongoLog depthLog = null;
            try {
                depthLog = logService.getMongoWitmlDepthLog(wellUid);
                depthLog = mongoLogService.fillLogDataNoReduceNoUserUOMSettings(0.0f, 50000f, depthLog);
                //Cache.set(key, depthLog, "15mn");
            } catch (Exception ex) {
                log.error("Error:", ex);
            }
            return avgPerStandCalculation.getAvgPerStandCalculation(depthLog, surveyList);
        }
        return surveyList;
    }
}
