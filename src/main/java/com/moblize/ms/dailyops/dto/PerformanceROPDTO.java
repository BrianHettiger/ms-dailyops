
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moblize.ms.dailyops.domain.PerformanceROP;
import lombok.*;

import java.io.Serializable;

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
public class PerformanceROPDTO {

    @JsonProperty("uid")
    public String uid;
    @JsonProperty("avgROP")
    public PerformanceROP.AvgROP avgROP;

    @Getter
    @Setter
    public static class AvgROP implements Serializable {
        public Section section;
    }

    @Getter
    @Setter
    public static class Section implements Serializable {
        public Double all;
        public Double surface;
        public Double intermediate;
        public Double curve;
        public Double lateral;
    }

}
