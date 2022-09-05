package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "channel",
    "conversionFactor"
})
@Data
public class ConvertChannel {

    @JsonProperty("channel")
    private String channel;
    @JsonProperty("conversionFactor")
    private Integer conversionFactor;

}
