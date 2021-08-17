package com.moblize.ms.dailyops.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScaledPlannedData implements Serializable {
    @ProtoField(number = 1)
    Double md;
    @ProtoField(number = 2)
    Double ew;
    @ProtoField(number = 3)
    Double ns;
    @ProtoField(number = 4)
    Double vs;
    @ProtoField(number = 5)
    Double tvd;
    @ProtoField(number = 6)
    Double dls;

}
