package com.moblize.ms.dailyops.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "wellSurveyPlannedLatLong")
public class WellSurveyPlannedLatLong {

    private String uid;
    private List<Float[]> drilledData;
    private List<Float[]> plannedData;


}
