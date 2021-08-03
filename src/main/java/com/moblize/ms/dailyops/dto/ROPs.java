
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonPropertyOrder({
    "section"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ROPs {

    @ProtoField(number = 1)
    @JsonProperty("aRop")
    public ROP avgROP;
    @ProtoField(number = 2)
    @JsonProperty("sRop")
    public ROP slidingROP;
    @ProtoField(number = 3)
    @JsonProperty("rRop")
    public ROP rotatingROP;
    @ProtoField(number = 4)
    @JsonProperty("eRop")
    public ROP effectiveROP;
    @ProtoField(number = 5)
    @JsonProperty("sp")
    private ROP slidingPercentage;
    @ProtoField(number = 6)
    @JsonProperty("fd")
    private ROP footageDrilled;


}
