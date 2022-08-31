package com.moblize.ms.dailyops.dto;

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
	private List<DrillingRoadMapWells> averageData = new ArrayList<>();
	private List<DrillingRoadMapWells> primaryWellDrillingRoadMap = new ArrayList<>();
	private List<DrillingRoadMapWells> formationBcwData = new ArrayList<>();
	private DrillingRoadMapWells currrentWellBcwFormationMap = new DrillingRoadMapWells();
	private DrillingRoadMapWells paceSetterFormationMap = new DrillingRoadMapWells();

}
