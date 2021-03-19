
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    public Double all;
    @JsonProperty("s")
    public Double surface;
    @JsonProperty("i")
    public Double intermediate;
    @JsonProperty("c")
    public Double curve;
    @JsonProperty("l")
    public Double lateral;

}
