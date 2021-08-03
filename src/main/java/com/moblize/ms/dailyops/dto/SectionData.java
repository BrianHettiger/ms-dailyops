package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SectionData {
    @ProtoField(number = 1)
    @JsonProperty("sec")
    WellDataSection section = new WellDataSection();
}
