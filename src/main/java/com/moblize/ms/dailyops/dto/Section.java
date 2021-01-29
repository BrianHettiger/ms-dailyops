
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "all",
    "surface",
    "intermediate",
    "curve",
    "lateral"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Section {

    @JsonProperty("all")
    public Double all;
    @JsonProperty("surface")
    public Double surface;
    @JsonProperty("intermediate")
    public Double intermediate;
    @JsonProperty("curve")
    public Double curve;
    @JsonProperty("lateral")
    public Double lateral;

}
