
package com.moblize.ms.dailyops.service.dto;

import javax.annotation.Generated;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moblize.ms.dailyops.domain.mongo.Intersection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "targetWindowsData",
    "footagePercentage"
})
@Setter @Getter @NoArgsConstructor
public class SectionPlanView {

    @JsonProperty("targetWindowsData")
    public TargetWindowsData targetWindowsData;
    @JsonProperty("footagePercentage")
    public Float footagePercentage;
}
