package com.moblize.ms.dailyops.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BHA {
    public long id;
    public String name;
    @JsonProperty("mds")
    public float mdStart;
    @JsonProperty("mde")
    public float mdEnd;
    @JsonProperty("fd")
    public float footageDrilled;
    @JsonProperty("ds")
    public String motorType;
    @JsonProperty("sec")
    public List<String> sections = new ArrayList<>();
    @JsonProperty("aRop")
    public RopType avgRop ;
    @JsonProperty("sRop")
    public RopType rotatingROP ;
    @JsonProperty("rRop")
    public RopType slidingROP ;
    @JsonProperty("eRop")
    public RopType effectiveROP;
    @JsonProperty("sp")
    public RopType slidePercentage;
    @JsonProperty("dls")
    public String avgDLS;
    @JsonProperty("angle")
    public Double buildWalkAngle;
    @JsonProperty("c_angle")
    public Double buildWalkCompassAngle;
    @JsonProperty("c_dir")
    public String buildWalkCompassDirection;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RopType implements Serializable {
        @JsonProperty("sec")
        public Section section;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Section implements Serializable {
        @JsonProperty("a")
        public double all;
        @JsonProperty("s")
        public double surface;
        @JsonProperty("i")
        public double intermediate;
        @JsonProperty("c")
        public double curve;
        @JsonProperty("l")
        public double lateral;
    }
}


