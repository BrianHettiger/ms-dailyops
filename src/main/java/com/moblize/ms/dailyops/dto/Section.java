
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

    @JsonProperty("a")
    public Integer all;
    @JsonProperty("s")
    public Integer surface;
    @JsonProperty("i")
    public Integer intermediate;
    @JsonProperty("c")
    public Integer curve;
    @JsonProperty("l")
    public Integer lateral;

}
