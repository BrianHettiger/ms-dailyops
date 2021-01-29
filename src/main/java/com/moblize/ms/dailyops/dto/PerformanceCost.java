
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uid",
    "cost"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceCost {

    @JsonProperty("uid")
    public String uid;
    @JsonProperty("cost")
    public Cost cost;

}
