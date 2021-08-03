package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.AlarmDetailClient;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotifyDPVAService {


    @Autowired
    private AlarmDetailClient alarmDetailClient;

    public void notifyDPVAJob(TargetWindowDPVA targetWindow){

        if(targetWindow.getIsEnable()){
            List<SurveyRecord> sureveyData = alarmDetailClient.drillerDashBoardBuildAnalysis(targetWindow.getUid());
        } else {

        }

    }
}
