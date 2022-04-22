package com.moblize.ms.dailyops.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BHA {
    public long id;
    public String name;
    @JsonProperty("mds")
    public int mdStart;
    @JsonProperty("mde")
    public int mdEnd;
    @JsonProperty("ds")
    public String motorType;
    @JsonProperty("hs")
    public Float holeSize;
    @JsonProperty("sec")
    public List<String> sections = new ArrayList<>();
    @JsonProperty("aRop")
    public RopType avgRop ;
    @JsonProperty("rRop")
    public RopType rotatingROP ;
    @JsonProperty("sRop")
    public RopType slidingROP ;
    @JsonProperty("eRop")
    public RopType effectiveROP;
    @JsonProperty("sp")
    public RopType slidePercentage;
    @JsonProperty("fd")
    public RopType footageDrilled;
    @JsonProperty("aDls")
    public RopType avgDLS;
    @JsonProperty("mYld")
    public RopType avgMotorYield;
    @JsonProperty("aDirAng")
    public RopType buildWalkCompassAngle;
    @JsonProperty("aDir")
    public DirectionType buildWalkCompassDirection;

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
        public Number all;
        @JsonProperty("s")
        public Number surface;
        @JsonProperty("i")
        public Number intermediate;
        @JsonProperty("c")
        public Number curve;
        @JsonProperty("l")
        public Number lateral;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DirectionType implements Serializable {
        @JsonProperty("sec")
        public SectionDirection section;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SectionDirection implements Serializable {
        @JsonProperty("a")
        public String all;
        @JsonProperty("s")
        public String surface;
        @JsonProperty("i")
        public String intermediate;
        @JsonProperty("c")
        public String curve;
        @JsonProperty("l")
        public String lateral;
    }
}


