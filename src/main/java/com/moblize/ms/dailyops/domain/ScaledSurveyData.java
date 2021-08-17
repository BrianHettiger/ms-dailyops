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
    Double vs;
    @ProtoField(number = 3)
    Double tvd;
    @ProtoField(number = 4)
    Double ew;
    @ProtoField(number = 5)
    Double ns;
    @ProtoField(number = 6)
    Double dls;
    @ProtoField(number = 7)
    Double distance;
    @ProtoField(number = 8)
    Boolean isIn;

}
