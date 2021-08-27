package com.moblize.ms.dailyops.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ScaledSurveyData implements Serializable {
    @ProtoField(number = 1)
    Double md;
    @ProtoField(number = 2)
    Double mdActual;
    @ProtoField(number = 3)
    Double vs;
    @ProtoField(number = 4)
    Double tvd;
    @ProtoField(number = 5)
    Double ew;
    @ProtoField(number = 6)
    Double ns;
    @ProtoField(number = 7)
    Double dls;
    // SECTION VIEW
    @ProtoField(number = 8)
    Double svDistance;
    @ProtoField(number = 9)
    Boolean svIsIn;
    // PLAN VIEW
    @ProtoField(number = 10)
    Double pvDistance;
    @ProtoField(number = 11)
    Boolean pvIsIn;
    @ProtoField(number = 12)
    Double azi;
    @ProtoField(number = 13)
    Double inc;
    @ProtoField(number = 14)
    Double dlsDistance;

}
