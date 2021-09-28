package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class BHACount {

    @ProtoField(number = 1)
    @JsonProperty("sec")
    public BHASectionCount section = new BHASectionCount();

}
