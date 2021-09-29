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
    Double all = 0D;
    @ProtoField(number = 2)
    @JsonProperty("s")
    Double surface = 0D;
    @ProtoField(number = 3)
    @JsonProperty("i")
    Double intermediate = 0D;
    @ProtoField(number = 4)
    @JsonProperty("c")
    Double curve = 0D;
    @ProtoField(number = 5)
    @JsonProperty("l")
    Double lateral = 0D;
}
