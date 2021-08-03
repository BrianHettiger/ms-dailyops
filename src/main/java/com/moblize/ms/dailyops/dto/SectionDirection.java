package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SectionDirection {
    @ProtoField(number = 1)
    @JsonProperty("a")
    String all = null;
    @ProtoField(number = 2)
    @JsonProperty("s")
    String surface = null;
    @ProtoField(number = 3)
    @JsonProperty("i")
    String intermediate = null;
    @ProtoField(number = 4)
    @JsonProperty("c")
    String curve = null;
    @ProtoField(number = 5)
    @JsonProperty("l")
    String lateral = null;
}
