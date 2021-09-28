package com.moblize.ms.dailyops.service.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.ProtoField;


@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class WellPlan {
    @ProtoField(number = 1)
    Long id;
    @ProtoField(number = 2)
    String wellUid;
    @ProtoField(number = 3)
    String wellboreUid;
    @ProtoField(number = 4)
    Double measuredDepth;
    @ProtoField(number = 5)
    Double inclination;
    @ProtoField(number = 6)
    Double azimuth;
    @ProtoField(number = 7)
    Double trueVerticalDepth;
    @ProtoField(number = 8)
    Double verticalSection;
    @ProtoField(number = 9)
    Double northSouth;
    @ProtoField(number = 10)
    Double eastWest;
    @ProtoField(number = 11)
    Double dogLeg;
    @ProtoField(number = 12)
    Double buildRate;
    @ProtoField(number = 13)
    Double turnRate;
}
