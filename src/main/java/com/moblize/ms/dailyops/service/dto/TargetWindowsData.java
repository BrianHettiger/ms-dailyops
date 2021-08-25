
package com.moblize.ms.dailyops.service.dto;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moblize.ms.dailyops.domain.mongo.Intersection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public List<List<Float>> firstLine = null;
    @JsonProperty("centerLine")
    public List<List<Float>> centerLine = null;
    @JsonProperty("lastLine")
    public List<List<Float>> lastLine = null;
    @JsonProperty("sideLines")
    public List<List<List<Float>>> sideLines = null;
    @JsonProperty("intersections")
    private List<Intersection> intersections = null;

}
