package com.moblize.ms.dailyops.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Last4WellsResponse {

    @ProtoField(number = 1)
    String uid;
    @ProtoField(number = 2)
    String name;
    @JsonProperty("sDate")
    @ProtoField(number = 3)
    Float spudDate;
    @ProtoField(number = 8)
    @JsonProperty("aRop")
    ROP avgROP;
    @ProtoField(number = 9)
    @JsonProperty("sRop")
    ROP slidingROP;
    @ProtoField(number = 10)
    @JsonProperty("rRop")
    ROP rotatingROP;
    @ProtoField(number = 11)
    @JsonProperty("eRop")
    ROP effectiveROP;
    @ProtoField(number = 12)
    @JsonProperty("tday")
    SectionData totalDays;
    @ProtoField(number = 13)
    @JsonProperty("fpday")
    SectionData footagePerDay;
    @ProtoField(number = 14)
    @JsonProperty("sp")
    ROP slidingPercentage;
    @ProtoField(number = 15)
    @JsonProperty("fd")
    ROP footageDrilled;
    @ProtoField(number = 16)
    @JsonProperty("aDls")
    SectionData avgDLSBySection;
    @ProtoField(number = 17)
    @JsonProperty("mYld")
    SectionData avgMYBySection;
    @ProtoField(number = 18)
    @JsonProperty("aDirAng")
    SectionData avgDirectionAngle;
    @ProtoField(number = 19)
    @JsonProperty("aDir")
    SectionDataDirection avgDirection;
    @JsonProperty("hs")
    Map<String, RangeData> holeSectionRange = Map.of("a", new RangeData(), "c", new RangeData(),"i", new RangeData(),"l", new RangeData(),"s", new RangeData());
    @JsonProperty("sectionConnections")
    Map<String,Double> sectionConnections;
    @JsonProperty("trippingData")
    Map<String, Map<String, Float>> trippingData;
}
