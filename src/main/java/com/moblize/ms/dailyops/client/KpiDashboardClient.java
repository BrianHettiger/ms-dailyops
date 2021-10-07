package com.moblize.ms.dailyops.client;

import com.moblize.ms.dailyops.service.dto.HoleSection;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Lazy
@FeignClient("kpidashboard")
public interface KpiDashboardClient {
    @GetMapping(value = "holesections/getholesections")
    List<HoleSection> getHoleSections(
        @RequestParam("wellUid") String wellUid
    );
}
