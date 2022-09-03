package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.domain.FormationMarker;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.utils.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Lazy
@FeignClient("witsmllogs")
public interface WitsmlLogsClient {

    @GetMapping(value = "formationmarkers/read")
    JSONResult getFormationMarkers(
        @RequestParam("wellUid") String wellUid,
        @RequestParam("wellboreUid") String wellboreUid
    );

    @Transactional(readOnly = true)
    @GetMapping(value = "formationmarkers/readAll")
    JSONResult getFormationMarkersForWells(
        @RequestParam("wellUids") List<String> wellUids,
        @RequestParam("wellboreUid") String wellboreUid
    );

    @GetMapping(value = "logs/getdepthlogforchannels")
    Map<String, Object> getdepthlogforchannels(
        @RequestParam("wellUid") String wellUid,
        @RequestParam("wellboreUid") String wellboreUid,
        @RequestParam("channels") List<String> channels,
        @RequestParam("startIndex") Float startIndex,
        @RequestParam("endIndex") Float endIndex,
        @RequestParam("sampling") int sampling,
        @RequestParam("indexChannels") Boolean indexChannels,
        @RequestParam("disableReduced") Boolean disableReduced
    );

    @GetMapping(value = "getDepthLog")
    MongoLog getDepthLog(@RequestParam("wellUid") String wellUid);

    @GetMapping(value = "logs/getdayvdepthlog")
    Object getDayVDepthLog(
        @RequestParam("wellUid") String wellUid,
        @RequestParam("wellboreUid") String wellboreUid,
        @RequestParam("filter") String filter,
        @RequestParam("scenarioId") String scenarioId,
        @RequestParam("isTimeSelected") Boolean isTimeSelected,
        @RequestParam("fromDate") String fromDate,
        @RequestParam("toDate") String toDate,
        @RequestParam("fromDepth") String fromDepth,
        @RequestParam("toDepth") String toDepth
    );
}
