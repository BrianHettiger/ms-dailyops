package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyRecord {

    Float previousMeasuredDepth;
    Float curMeasuredDepth;
    Float startIncl;
    Float incl;
    Float startAzimuth;
    Float azimuth;
    Float tvd;
    Float dls;
    Float bldRate;
    Float wlkRate;
    Float avgRopBySliding;
    Float avgWobBySliding;
    Float avgDiffPressureBySliding;
    Float avgTorqueBySliding;
    Float avgRpmBySliding;
    Float avgMudFlowInBySliding;
    Float avgGammaRay1ShiftedBySliding;
    Float avgRopByRotaryDrilling;
    Float avgWobByRotaryDrilling;
    Float avgDiffPressureByRotaryDrilling;
    Float avgTorqueByRotaryDrilling;
    Float avgRpmByRotaryDrilling;
    Float avgMudFlowInByRotaryDrilling;
    Float avgGammaRay1ShiftedByRotaryDrilling;
    HoleSectionDTO.HoleSectionType section;
    Double slidingPercentage;
    Float dispEW;
    Float dispNS;
    Float vertsect;
    Float mdInf;
    Float delmd;

}
