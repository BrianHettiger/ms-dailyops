package com.moblize.ms.dailyops.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"offsetWellUids",
    "distance"}
)
@Data
public class OffSetWellByDistance {

    @JsonProperty("offsetWells")
    private List<OffsetWell> offsetWells = null;
    @JsonProperty("miles")
    private Long miles;
    @JsonProperty("offsetWellUids")
    private List<String> offsetWellUids;
    @JsonProperty("distance")
    private Long distance;
    private OffSetWellByDistance wellListByDistance;
}
