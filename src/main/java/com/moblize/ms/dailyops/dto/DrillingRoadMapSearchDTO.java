package com.moblize.ms.dailyops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrillingRoadMapSearchDTO {
	String primaryWellUid;
	List<String> offsetWellUids;
	String calculationType;
	String wellboreUid;
}
