package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Drilling Road map Data for display
 */
@Data @Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"bcwData"})
public class DrillingRoadmapJsonResponse {

	private String currentMeasuredDepth;
	private String currenttvd;
	private String currentInclination;
	private String currentAzimuth;
	private String paceSetterWellName;
	private String paceSetterWellUid;
	private String paceSetterWellboreUid;
	private String currentRigState;
	private String currentSection;
	private String currentWellMudWeight;
	private String currentWellEndIndex;
	private String currentWellDepth;
	private String currentWellFormation;
	private String daysVsAEF;
	private transient List<DrillingRoadMapWells> bcwData = new ArrayList<>();
	private List<AverageData> averageData = new ArrayList<>();
	private List<DrillingRoadMapWells> primaryWellDrillingRoadMap = new ArrayList<>();
	private List<DrillingRoadMapWells> formationBcwData = new ArrayList<>();
	private DrillingRoadMapWells currrentWellBcwFormationMap = new DrillingRoadMapWells();
	private DrillingRoadMapWells paceSetterFormationMap = new DrillingRoadMapWells();

    @Data
    public static class AverageData{
        private String FormationName;
        private String MudFlowInAvg;
        private String SurfaceTorqueMax;
        private String PumpPress;
        private String WeightonBitMax;
        private String ROPAvg;
        private String RPMA;
        private String DiffPressure;
    }

}
