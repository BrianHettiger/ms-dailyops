package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.utils.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
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


}
