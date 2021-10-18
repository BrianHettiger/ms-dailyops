
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

@JsonPropertyOrder({
    "a",
    "s",
    "i",
    "c",
    "l"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Section {

    @ProtoField(number = 1)
    @JsonProperty("a")
    public Double all = null;
    @ProtoField(number = 2)
    @JsonProperty("s")
    public Double surface = null;
    @ProtoField(number = 3)
    @JsonProperty("i")
    public Double intermediate = null;
    @ProtoField(number = 4)
    @JsonProperty("c")
    public Double curve = null;
    @ProtoField(number = 5)
    @JsonProperty("l")
    public Double lateral = null;

}
