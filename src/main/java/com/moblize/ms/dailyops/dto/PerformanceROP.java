
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uid",
    "avgROP"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceROP {

    @JsonProperty("uid")
    public String uid;
    @JsonProperty("avgROP")
    public AvgROP avgROP;

}
