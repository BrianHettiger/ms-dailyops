package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.service.dto.HoleSection;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Lazy
@FeignClient("kpidashboard")
public interface KpiDashboardClient {
    @GetMapping(value = "holesections/getholesections")
    List<HoleSection> getHoleSections(
        @RequestParam("wellUid") String wellUid
    );
    @PostMapping(value = "kpiTagRopBasedOnWells")
    Object kpiTagRopBasedOnWells(
        @RequestParam(value = "addDepthRange") Boolean addDepthRange,
        @RequestParam(value = "sectionName") String sectionName,
        @RequestBody Map<String, Object> parameters
    );
}
