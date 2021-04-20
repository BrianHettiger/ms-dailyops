
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

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

    @JsonProperty("a")
    public Number all = null;
    @JsonProperty("s")
    public Number surface = null;
    @JsonProperty("i")
    public Number intermediate = null;
    @JsonProperty("c")
    public Number curve = null;
    @JsonProperty("l")
    public Number lateral = null;

}
