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
  //  private String customer;
    private String wellStatus;
    private List<ScaledPlannedData> plannedData = new ArrayList<>();
    private List<ScaledSurveyData> surveyData = new ArrayList<>();
   // private Float svInPercentage = 0.0f;
   // private Float pvInPercentage = 0.0f;
 //   private TargetWindowPerFootDTO targetWindow = new TargetWindowPerFootDTO();
    private DonutDistanceDTO donutDistance;
    private Float incAngle = 0.0f;
    private Float azmAngle = 0.0f;

    @JsonProperty("sectionView")
    public SectionPlanView sectionView;

    @JsonProperty("planView")
    public SectionPlanView planView;
}
