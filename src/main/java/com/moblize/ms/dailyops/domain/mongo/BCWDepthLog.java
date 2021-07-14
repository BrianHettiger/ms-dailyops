package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "bcwDepthLog")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class BCWDepthLog {

    @Id
    private String id;
    public String uid;
    private double highestRopSum;
    private int highestRopCount;
    private double highestRopAvg;
    private double diffPressureSum;
    private int diffPressureCount;
    private double diffPressureAvg;
    private double mudFlowSum;
    private int mudFlowCount;
    private double mudFlowAvg;
    private double pumpPressureSum;
    private int pumpPressureCount;
    private double pumpPressureAvg;
    private double surfaceTorqueSum;
    private int surfaceTorqueCount;
    private double surfaceTorqueAvg;
    private double weightOnBitSum;
    private int weightOnBitCount;
    private double weightOnBitAvg;
    private double rpmaSum;
    private int rpmaCount;
    private double rpmaAvg;

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
    public Double rOPAvg;
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
