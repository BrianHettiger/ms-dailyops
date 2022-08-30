package com.moblize.ms.dailyops.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Drilling Road map Data for display
 */
@Data
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
	private DrillingRoadmapJsonResponse.DrillingRoadMapWells currrentWellBcwFormationMap = new DrillingRoadmapJsonResponse.DrillingRoadMapWells();
	private DrillingRoadMapWells paceSetterFormationMap = new DrillingRoadMapWells();

	public void buildFormationBcwData() {
		Map<String, DrillingRoadMapWells> formationBcwWellMap = new HashMap<String, DrillingRoadMapWells>();
		for (DrillingRoadMapWells well : bcwData) {
			DrillingRoadMapWells bcwWell = formationBcwWellMap.get(well.getFormationName());
			if (bcwWell == null) {
				formationBcwWellMap.put(well.getFormationName(), well);
			}
			else {
				if (Double.valueOf(well.getROPAvg()).doubleValue() > Double.valueOf(bcwWell.getROPAvg()).doubleValue()) {
					formationBcwWellMap.put(well.getFormationName(), well);
				}
			}
		}
		for (DrillingRoadMapWells bcwWell : formationBcwWellMap.values()) {
			formationBcwData.add(bcwWell);
		}
	}

    @Data
	public static class DrillingRoadMapWells {
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
}
