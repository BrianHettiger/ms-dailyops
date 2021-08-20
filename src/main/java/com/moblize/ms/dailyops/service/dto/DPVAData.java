package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moblize.ms.dailyops.domain.ScaledPlannedData;
import com.moblize.ms.dailyops.domain.ScaledSurveyData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DPVAData {
    private String wellUid;
    private String customer;
    private String wellStatus;
    private List<ScaledPlannedData> plannedData = new ArrayList<>();
    private List<ScaledSurveyData> surveyData = new ArrayList<>();
    private DonutDistanceDTO donutDistanceDTO;
}
