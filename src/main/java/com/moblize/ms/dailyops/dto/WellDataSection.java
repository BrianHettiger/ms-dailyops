package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WellDataSection {
    @JsonProperty("a")
    @ProtoField(number = 1)
    Double all;
    @ProtoField(number = 2)
    @JsonProperty("s")
    Double surface;
    @ProtoField(number = 3)
    @JsonProperty("i")
    Double intermediate;
    @ProtoField(number = 4)
    @JsonProperty("c")
    Double curve;
    @ProtoField(number = 5)
    @JsonProperty("l")
    Double lateral;
}
