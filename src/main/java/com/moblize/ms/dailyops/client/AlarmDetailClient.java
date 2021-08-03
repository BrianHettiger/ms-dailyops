package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Lazy
@FeignClient("alarmdetail")
public interface AlarmDetailClient {

    @GetMapping(value ="api/v1/drillerDashBoardBuildAnalysis/{wellUid}")
    List<SurveyRecord> drillerDashBoardBuildAnalysis(
        @PathVariable("wellUid") String wellUid);

}
