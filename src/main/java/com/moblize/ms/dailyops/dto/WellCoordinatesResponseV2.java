package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WellCoordinatesResponseV2 {

    @ProtoField(number = 1)
    String uid;
    @ProtoField(number = 2)
    String name;
    @JsonProperty("sDate")
    @ProtoField(number = 3)
    Float spudDate;
    @ProtoField(number = 4)
    Integer distinctBHAsUsedCount = 0;
    @ProtoField(number = 5)
    String activeRigName;
    @ProtoField(number = 6)
    Location location;
    @ProtoField(number = 7)
    @JsonIgnore
    String statusWell;
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
    Map<String, RangeData> holeSectionRange;
    @ProtoField(number = 21)
    Cost cost;
    @ProtoField(number = 22)
    BHACount bhaCount;
    @ProtoField(number = 23)
    List<String> drilledData = new ArrayList<>();
    @ProtoField(number = 24)
    List<String> plannedData = new ArrayList<>();
    @ProtoField(number = 20)
    @JsonIgnore
    List<String> rangeDataKeys;
    @ProtoField(number = 25)
    @JsonIgnore
    List<RangeData> rangeDataValues;
    public void setProtoData() {
        if(rangeDataKeys == null) {
            rangeDataKeys = new ArrayList<>();
            rangeDataValues = new ArrayList<>();
            holeSectionRange.forEach((k, v) -> {
                rangeDataKeys.add(k);
                rangeDataValues.add(v);
            });
        }
    }
    public void setEntries() {
        if(holeSectionRange == null && rangeDataKeys != null) {
            holeSectionRange = new HashMap<>();
            for(int i = 0; i < rangeDataKeys.size(); i++) {
                holeSectionRange.put(rangeDataKeys.get(i), rangeDataValues.get(i));
            }
        }
    }
}
