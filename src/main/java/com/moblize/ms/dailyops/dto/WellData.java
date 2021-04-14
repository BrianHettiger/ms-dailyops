package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private WellData.SectionData avgDLSBySection;
    @JsonProperty("mYld")
    private WellData.SectionData avgMYBySection;
    @JsonProperty("aDirAng")
    private WellData.SectionData avgDirectionAngle;
    @JsonProperty("aDir")
    private WellData.SectionDataDirection avgDirection;
    @JsonProperty("hs")
    private Map<String, RangeData> holeSectionRange = new HashMap<>();

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionData {
        @JsonProperty("sec")
        private Section section = new Section();
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Section {
        @JsonProperty("a")
        private Number all;
        @JsonProperty("s")
        private Number surface;
        @JsonProperty("i")
        private Number intermediate;
        @JsonProperty("c")
        private Number curve;
        @JsonProperty("l")
        private Number lateral;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RangeData {
        @JsonProperty("mds")
        public int mdStart = 0;
        @JsonProperty("mde")
        public int mdEnd = 0;
        @JsonProperty("len")
        public int footageDrilled = 0;
    }


    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionDataDirection {
        @JsonProperty("sec")
        private SectionDirection section = new SectionDirection();
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionDirection {
        @JsonProperty("a")
        private String all  = null;
        @JsonProperty("s")
        private String surface = null ;
        @JsonProperty("i")
        private String intermediate = null ;
        @JsonProperty("c")
        private String curve = null;
        @JsonProperty("l")
        private String lateral = null;
    }
}
