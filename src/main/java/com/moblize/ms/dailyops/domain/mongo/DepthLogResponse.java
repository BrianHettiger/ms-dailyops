package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "index",
    "DATE",
    "BitDepth",
    "HoleDepth",
    "BlockPostion",
    "ROPAvg",
    "HookloadMax",
    "WeightonBitMax",
    "SurfaceTorqueMax",
    "RPMA",
    "PumpPressure",
    "Pump1",
    "Pump2",
    "MudFlowOutPercent",
    "MudFlowInAvg",
    "TotalGas",
    "DiffPressure",
    "Inclination",
    "AzimuthCorr",
    "MagToolFace",
    "GravToolface",
    "GammaRay1Corr",
    "RigState",
    "offSetWellUid"
})
public class DepthLogResponse {

    @JsonProperty("index")
    public Double index;
    @JsonProperty("DATE")
    public Long date;
    @JsonProperty("BitDepth")
    public Double bitDepth;
    @JsonProperty("HoleDepth")
    public Double holeDepth;
    @JsonProperty("BlockPostion")
    public Double blockPostion;
    @JsonProperty("ROPAvg")
    public Double ropAvg;
    @JsonProperty("HookloadMax")
    public Double hookloadMax;
    @JsonProperty("WeightonBitMax")
    public Double weightonBitMax;
    @JsonProperty("SurfaceTorqueMax")
    public Double surfaceTorqueMax;
    @JsonProperty("RPMA")
    public Double rpma;
    @JsonProperty("PumpPressure")
    public Double pumpPressure;
    @JsonProperty("Pump1")
    public Double pump1;
    @JsonProperty("Pump2")
    public Double pump2;
    @JsonProperty("MudFlowOutPercent")
    public Double mudFlowOutPercent;
    @JsonProperty("MudFlowInAvg")
    public Double mudFlowInAvg;
    @JsonProperty("TotalGas")
    public Double totalGas;
    @JsonProperty("DiffPressure")
    public Double diffPressure;
    @JsonProperty("Inclination")
    public Integer inclination;
    @JsonProperty("AzimuthCorr")
    public Integer azimuthCorr;
    @JsonProperty("MagToolFace")
    public Integer magToolFace;
    @JsonProperty("GravToolface")
    public Integer gravToolface;
    @JsonProperty("GammaRay1Corr")
    public Integer gammaRay1Corr;
    @JsonProperty("RigState")
    public String rigState;
    @JsonProperty("offSetWellUid")
    public String offSetWellUid;

}
