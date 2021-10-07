package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.dto.TortuosityRequestDTO;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TortuosityService {

    @Autowired
    private AlarmDetailClient alarmDetailClient;
    public List<SurveyRecord> getTortuosityIndex(TortuosityRequestDTO requestDTO){
        List<SurveyRecord> surveyRecords = alarmDetailClient.drillerDashBoardBuildAnalysis(requestDTO.primaryWellUid);
        return surveyRecords;
    }

}
