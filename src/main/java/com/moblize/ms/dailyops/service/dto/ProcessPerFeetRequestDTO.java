package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPerFeetRequestDTO {

    private TargetWindowDPVA targetWindow;
    private List<SurveyRecord> sureveyData;
    private List<WellPlan> plannedData;
    private String dataUpdateFor;
}
