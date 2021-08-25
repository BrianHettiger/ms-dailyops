package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moblize.ms.dailyops.domain.ScaledPlannedData;
import com.moblize.ms.dailyops.domain.ScaledSurveyData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.infinispan.protostream.annotations.ProtoField;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DPVAData {
    private String wellUid;
    private String wellStatus;
    private List<ScaledPlannedData> plannedData = new ArrayList<>();
    private List<ScaledSurveyData> surveyData = new ArrayList<>();
    private DonutDistanceDTO donutDistance;

    @JsonProperty("sectionView")
    public SectionPlanView sectionView;

    @JsonProperty("planView")
    public SectionPlanView planView;
}
