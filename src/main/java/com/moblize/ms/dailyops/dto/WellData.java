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
    private SectionData avgDLSBySection;
    @JsonProperty("mYld")
    private SectionData avgMYBySection;
    @JsonProperty("aDirAng")
    private SectionData avgDirectionAngle;
    @JsonProperty("aDir")
    private SectionDataDirection avgDirection;
    @JsonProperty("hs")
    private Map<String, RangeData> holeSectionRange = new HashMap<>();


}
