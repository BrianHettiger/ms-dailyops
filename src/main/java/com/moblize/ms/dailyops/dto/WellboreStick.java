package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class WellboreStick {

    private String uid;
    private AvgROP avgROP;
    private Cost cost;
    private List<Map<String,Object>> drilledData;
    private List<Map<String,Object>> plannedData;

}
