package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SectionDataDirection {
    @ProtoField(number = 1)
    @JsonProperty("sec")
    SectionDirection section = new SectionDirection();
}
