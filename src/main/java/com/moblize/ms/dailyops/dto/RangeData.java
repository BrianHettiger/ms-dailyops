package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RangeData {
    @ProtoField(number = 1)
    @JsonProperty("mds")
    public Integer mdStart = 0;
    @ProtoField(number = 2)
    @JsonProperty("mde")
    public Integer mdEnd = 0;
    @ProtoField(number = 3)
    @JsonProperty("len")
    public Integer footageDrilled = 0;
    @ProtoField(number = 4)
    @JsonProperty("tvds")
    public Integer tvDepthStart = 0;
    @ProtoField(number = 5)
    @JsonProperty("tvde")
    public Integer tvDepthEnd = 0;
}
