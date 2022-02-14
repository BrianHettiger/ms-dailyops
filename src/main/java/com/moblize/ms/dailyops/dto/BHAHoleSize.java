package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class BHAHoleSize {

    @ProtoField(number = 1)
    @JsonProperty("sec")
    public BHASectionHoleSize section = new BHASectionHoleSize();
}
