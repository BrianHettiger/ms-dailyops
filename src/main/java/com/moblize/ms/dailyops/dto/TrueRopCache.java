package com.moblize.ms.dailyops.dto;

import lombok.Data;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Data
public class TrueRopCache extends TrueRopData implements Serializable {
    @ProtoField(number = 1)
    String wellUid;
    @ProtoField(number = 2)
    Long firstRec;
    @ProtoField(number = 3)
    Long lastRec;

}
