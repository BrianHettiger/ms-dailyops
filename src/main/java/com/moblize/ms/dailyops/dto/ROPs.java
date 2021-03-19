
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "section"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ROPs {

    @JsonProperty("aRop")
    public ROP avgROP;
    @JsonProperty("sRop")
    public ROP slidingROP;
    @JsonProperty("rRop")
    public ROP rotatingROP;
    @JsonProperty("eRop")
    public ROP effectiveROP;
    @JsonProperty("sp")
    private ROP slidingPercentage;
    @JsonProperty("fd")
    private ROP footageDrilled;


    @Getter
    @Setter
    public static class ROP implements Serializable {
        @JsonProperty("sec")
        public Section section = new Section();
    }



}
