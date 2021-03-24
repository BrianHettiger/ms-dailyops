package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonProperty("aDirAng")
    private WellData.SectionData avgDirectionAngle;
    @JsonProperty("aDir")
    private WellData.SectionDataDirection avgDirection;
    @JsonProperty("hs")
    private Map<String, RangeData> holeSectionRange = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionData {
        @JsonProperty("sec")
        private Section section = new Section();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Section {
        @JsonProperty("a")
        private Double all = 0D;
        @JsonProperty("s")
        private Double surface = 0D;
        @JsonProperty("i")
        private Double intermediate = 0D;
        @JsonProperty("c")
        private Double curve = 0D;
        @JsonProperty("l")
        private Double lateral = 0D;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        @JsonProperty("fd")
        public int footageDrilled = 0;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionDataDirection {
        @JsonProperty("sec")
        private SectionDirection section = new SectionDirection();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionDirection {
        @JsonProperty("a")
        private String all = "";
        @JsonProperty("s")
        private String surface = "";
        @JsonProperty("i")
        private String intermediate = "";
        @JsonProperty("c")
        private String curve = "";
        @JsonProperty("l")
        private String lateral = "";
    }
}
