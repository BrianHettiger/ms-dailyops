package com.moblize.ms.dailyops.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Rig implements Serializable {
    @ProtoField(number = 1)
    String rigId;
    @ProtoField(number = 2)
    Long startDate;
}
