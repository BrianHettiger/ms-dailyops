package com.moblize.ms.dailyops.service.dto;

import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ScaledTargetWindow implements Serializable {
    @ProtoField(number = 1)
    Double md;
    @ProtoField(number = 2)
    Double vs;
    @ProtoField(number = 3)
    Double topTvd;
    @ProtoField(number = 4)
    Double bottomTvd;
    @ProtoField(number = 5)
    Double leftEw;
    @ProtoField(number = 6)
    Double rightEw;

}
