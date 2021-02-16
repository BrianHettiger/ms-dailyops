
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
    public Integer all;
    @JsonProperty("surface")
    public Integer surface;
    @JsonProperty("intermediate")
    public Integer intermediate;
    @JsonProperty("curve")
    public Integer curve;
    @JsonProperty("lateral")
    public Integer lateral;

}
