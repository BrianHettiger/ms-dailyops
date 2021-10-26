package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
