package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WellData {

    private String uid;
    @JsonProperty("tday")
    private SectionData totalDays = new SectionData();
    @JsonProperty("fpday")
    private SectionData footagePerDay = new SectionData();
    @JsonProperty("aDls")
    private SectionData avgDLSBySection = new SectionData();
    @JsonProperty("mYld")
    private SectionData avgMYBySection = new SectionData();
    @JsonProperty("aDirAng")
    private SectionData avgDirectionAngle = new SectionData();
    @JsonProperty("aDir")
    private SectionDataDirection avgDirection = new SectionDataDirection();
    @JsonProperty("hs")
    private Map<String, RangeData> holeSectionRange = new HashMap<>();


}
