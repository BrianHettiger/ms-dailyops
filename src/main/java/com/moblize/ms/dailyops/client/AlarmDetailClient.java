package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.service.dto.SurveyRecord;
import com.moblize.ms.dailyops.service.dto.WellPlan;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Lazy
@FeignClient("alarmdetail")
public interface AlarmDetailClient {

    @GetMapping(value ="api/v1/drillerDashBoardBuildAnalysis/{wellUid}")
    List<SurveyRecord> drillerDashBoardBuildAnalysis(
        @PathVariable("wellUid") String wellUid);

    @GetMapping("api/v1/getSurveyData/{wellUid}/{wellStatus}")
    List<SurveyRecord> getSurveyData(
        @PathVariable("wellUid") String wellUid,
        @PathVariable("wellStatus") String wellStatus
    );

    @GetMapping(value ="api/v1/getPlanData/{wellUid}/{wellStatus}")
    List<WellPlan> getPlanData(
        @PathVariable("wellUid") String wellUid,
        @PathVariable("wellStatus") String wellStatus
    );

    @GetMapping("api/v1/getLastSurveyData/{wellUid}/{wellStatus}")
    SurveyRecord getLastSurveyData(
        @PathVariable("wellUid") String wellUid,
        @PathVariable("wellStatus") String wellStatus
    );

}
