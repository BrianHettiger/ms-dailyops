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
    private Long id;
    @ProtoField(number = 2)
    private String wellUid;
    @ProtoField(number = 3)
    private String wellboreUid;
    @ProtoField(number = 4)
    private double measuredDepth;
    @ProtoField(number = 5)
    private double inclination;
    @ProtoField(number = 6)
    private double azimuth;
    @ProtoField(number = 7)
    private double trueVerticalDepth;
    @ProtoField(number = 8)
    private double verticalSection;
    @ProtoField(number = 9)
    private double northSouth;
    @ProtoField(number = 10)
    private double eastWest;
    @ProtoField(number = 11)
    private double dogLeg;
    @ProtoField(number = 12)
    private double buildRate;
    @ProtoField(number = 13)
    private double turnRate;
}
