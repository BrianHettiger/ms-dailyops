package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.dto.TrippingCasingRecordDTO;
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



    @GetMapping(value = "last4wellsconnection")
    Map<String,Double> getSectionConnections(
        @RequestParam("wellUidList") String wellUid
    );


    @GetMapping(value = "kpitagtrippingcasingextractionbywellid")
    Map<String, Map<String, Map<HoleSection.HoleSectionType, Float>>> getKpiExtractionByWellId(
        @RequestParam("wellUidList") String wellUid

    @PostMapping(value = "kpiTagRopBasedOnWells")
    Object kpiTagRopBasedOnWells(
        @RequestParam(value = "addDepthRange") Boolean addDepthRange,
        @RequestParam(value = "sectionName") String sectionName,
        @RequestBody Map<String, Object> parameters

    );
}
