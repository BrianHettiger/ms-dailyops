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
    @JsonProperty("hs")
    private Map<String, RangeData> holeSectionRange = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionData{
        @JsonProperty("sec")
        private Section section = new Section();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Section{
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
    public static class RangeData{
        @JsonProperty("mds")
        public float mdStart;
        @JsonProperty("mde")
        public float mdEnd;
        @JsonProperty("fd")
        public float footageDrilled;



    }
}
