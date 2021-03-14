package com.moblize.ms.dailyops.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class BHA {
    public long id;
    public String name;
    @JsonProperty("mds")
    public float mdStart;
    @JsonProperty("mde")
    public float mdEnd;
    @JsonProperty("fd")
    public float footageDrilled;
    @JsonProperty("ds")
    public String motorType;
    @JsonProperty("aRop")
    public Double avgROP;
    @JsonProperty("sRop")
    public Double slidingROP;
    @JsonProperty("rRop")
    public Double rotatingROP;
    @JsonProperty("eRop")
    public Double effectiveROP;
    @JsonProperty("sp")
    public Double slidePercentage;
    @JsonProperty("dls")
    public String avgDLS;
    @JsonProperty("angle")
    public Double buildWalkAngle;
    @JsonProperty("c_angle")
    public Double buildWalkCompassAngle;
    @JsonProperty("c_dir")
    public String buildWalkCompassDirection;
}


