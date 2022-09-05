package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Dist {
    @JsonProperty("calculated")
    private Double calculated;
}
