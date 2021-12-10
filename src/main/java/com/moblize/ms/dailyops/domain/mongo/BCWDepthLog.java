package com.moblize.ms.dailyops.domain.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BCWDepthLog {

    private String id;
    public String uid;
    public Double holeDepth;
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


}
