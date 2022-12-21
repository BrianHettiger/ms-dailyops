/*
 * Copyright (C) Moblize, Inc - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 */
package com.moblize.ms.dailyops.service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MudProperties {
    @JsonProperty("wellUid")
    private String wellUid;
    @JsonProperty("mudDataList")
    private List<MudData> mudDataList;

    @Data
    public static class MudData {
        private String dailyId;
        private Float mudWeight;
        private Float depth;
        private Float apiWaterLoss;
        private String companyName;
        private String checkDate;
        private String fluidName;
        private Float gels10Min;
        private Float gels10Sec;
        private Float gels30Min;
        private Float percentWater;
        private Float percentOil;
        private Float plasticViscosity;
        private Float viscosityFunnel;
        private Float yieldPoint;
        private Float percentHighGravitySolids;
        private Float percentLowGravitySolids;
        private Float phValue;
        private Float chloridesConc;
        private Float electroStaticStability;
        private Float hthpWaterLoss;
        private String phase;
        private Float filterCakeHthp;
        private Float filterCakeLtlp;
        private Long reportDate;
    }
}
