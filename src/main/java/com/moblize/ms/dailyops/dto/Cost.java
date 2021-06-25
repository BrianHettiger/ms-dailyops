
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonPropertyOrder({
    "afe",
    "perFt",
    "perLatFt",
    "total"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Cost {

    @ProtoField(number = 1)
    @JsonProperty("afe")
    public Double afe = null;
    @ProtoField(number = 2)
    @JsonProperty("perFt")
    public Double perFt  = null;
    @ProtoField(number = 3)
    @JsonProperty("perLatFt")
    public Double perLatFt  = null;
    @ProtoField(number = 4)
    @JsonProperty("total")
    public Double total = null;

}
