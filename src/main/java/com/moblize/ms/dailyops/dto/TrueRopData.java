package com.moblize.ms.dailyops.dto;

import lombok.Data;
import org.infinispan.protostream.annotations.ProtoField;

@Data
public class TrueRopData {
    @ProtoField(number = 4)
    Float min;
    @ProtoField(number = 5)
    Float max;
    @ProtoField(number = 6)
    Float range;
}
