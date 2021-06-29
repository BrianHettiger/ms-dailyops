package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WellCoordinatesResponse {

    private String uid;
    private String name;
    private Location location;
    private ROP avgROP;
    private Cost cost;
    private List<List<Double>> drilledData;
    private List<List<Double>> plannedData;
    private Integer distinctBHAsUsedCount = 0;
    private String activeRigName;
    @JsonIgnore
    private String statusWell;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class Location implements Serializable {

        private Double lng = 0.0;

        private Double lat = 0.0;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ROP implements Serializable {
        public Section section = new Section();
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Section {
        public Number all = null;
        public Number surface = null;
        public Number intermediate = null;
        public Number curve = null;
        public Number lateral = null;
    }
}
