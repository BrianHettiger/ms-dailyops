
package com.moblize.ms.dailyops.service.dto;

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
    "firstLine",
    "centerLine",
    "lastLine",
    "sideLines"
})
@Setter
@Getter
@NoArgsConstructor
public class TargetWindowsData {

    @JsonProperty("firstLine")
    public List<List<Float>> firstLine = new ArrayList<>();
    @JsonProperty("centerLine")
    public List<List<Float>> centerLine = new ArrayList<>();
    @JsonProperty("lastLine")
    public List<List<Float>> lastLine = new ArrayList<>();
    @JsonProperty("sideLines")
    public List<List<List<Float>>> sideLines = new ArrayList<>();
    @JsonProperty("intersections")
    private List<Intersection> intersections = new ArrayList<>();

}
