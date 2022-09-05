package com.moblize.ms.dailyops.dto;

import com.moblize.ms.dailyops.service.dto.HoleSection;
import com.moblize.ms.dailyops.service.dto.WellPlan;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class BCWDTO {
    private String primaryWellUid;
    private Long distance;
    private List<String> offsetWellUids;
    private String calculationType;
    private String wellboreUid;
    private Set<String> wellUID;
    private Map<String,List<HoleSection>> wellWiseHoleSectionList;
    private Map<String,List<BHA>> wellWiseBHAList;
    private Map<String, List<WellPlan>> wellsMongoLog;
    private OffSetWellByDistance wellListByDistance;
    private int bcwCount;


    private Map<String, Double> wellROP;
}
