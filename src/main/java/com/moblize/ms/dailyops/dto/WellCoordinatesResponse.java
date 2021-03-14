package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WellCoordinatesResponse {

    private String uid;
    private String name;
    private Location location;
    @JsonIgnore
    private String statusWell;
    @JsonProperty("aRop")
    private ROPs.ROP avgROP;
    @JsonProperty("sRop")
    private ROPs.ROP slidingROP;
    @JsonProperty("rRop")
    private ROPs.ROP rotatingROP;
    @JsonProperty("eRop")
    private ROPs.ROP effectiveROP;
    private Cost cost;
    private BHACount bhaCount;
    private List<Object> drilledData = new ArrayList<>();
    private List<Object> plannedData = new ArrayList<>();
    private Integer distinctBHAsUsedCount = 0;
    private String activeRigName;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Location implements Serializable {

        private Float lng = 0.0f;

        private Float lat = 0.0f;

    }
}
