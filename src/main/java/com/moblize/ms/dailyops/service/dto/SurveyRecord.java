package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.ProtoEnumValue;
import org.infinispan.protostream.annotations.ProtoField;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyRecord {

    @ProtoField(number = 1)
    Float previousMeasuredDepth;
    @ProtoField(number = 2)
    Float curMeasuredDepth;
    @ProtoField(number = 3)
    Float startIncl;
    @ProtoField(number = 4)
    Float incl;
    @ProtoField(number = 5)
    Float startAzimuth;
    @ProtoField(number = 6)
    Float azimuth;
    @ProtoField(number = 7)
    Float tvd;
    @ProtoField(number = 8)
    Float dls;
    @ProtoField(number = 9)
    Float bldRate;
    @ProtoField(number = 10)
    Float wlkRate;
    @ProtoField(number = 11)
    Float avgRopBySliding;
    @ProtoField(number = 12)
    Float avgWobBySliding;
    @ProtoField(number = 13)
    Float avgDiffPressureBySliding;
    @ProtoField(number = 14)
    Float avgTorqueBySliding;
    @ProtoField(number = 15)
    Float avgRpmBySliding;
    @ProtoField(number = 16)
    Float avgMudFlowInBySliding;
    @ProtoField(number = 17)
    Float avgGammaRay1ShiftedBySliding;
    @ProtoField(number = 18)
    Float avgRopByRotaryDrilling;
    @ProtoField(number = 19)
    Float avgWobByRotaryDrilling;
    @ProtoField(number = 20)
    Float avgDiffPressureByRotaryDrilling;
    @ProtoField(number = 21)
    Float avgTorqueByRotaryDrilling;
    @ProtoField(number = 22)
    Float avgRpmByRotaryDrilling;
    @ProtoField(number = 23)
    Float avgMudFlowInByRotaryDrilling;
    @ProtoField(number = 24)
    Float avgGammaRay1ShiftedByRotaryDrilling;
    @ProtoEnumValue(number = 25)
    HoleSection.HoleSectionType section;
    @ProtoField(number = 26)
    Double slidingPercentage;
    @ProtoField(number = 27)
    Float dispEW;
    @ProtoField(number = 28)
    Float dispNS;
    @ProtoField(number = 29)
    Float vertsect;

}
