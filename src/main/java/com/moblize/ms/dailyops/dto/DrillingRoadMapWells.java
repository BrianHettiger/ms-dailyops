package com.moblize.ms.dailyops.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DrillingRoadMapWells {

    private String wellUid;
    private String MD;
    private String FormationName;

    private String MudFlowInAvg;
    private String SurfaceTorqueMax;
    private String PumpPress;
    private String WeightonBitMax;
    private String ROPAvg;
    private String holeSize;
    private String RPMA;
    private String DiffPressure;
    private String AnnotationText;
}
