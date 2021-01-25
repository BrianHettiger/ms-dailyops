package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    private AvgROP avgROP;
    private Cost cost;
    private List<Map<String,Object>> drilledData;
    private List<Map<String,Object>> plannedData;



    @Getter
    @Setter
    @AllArgsConstructor
    public static class Location implements Serializable {

        private Float lng = 0.0f;

        private Float lat = 0.0f;

    }
}
