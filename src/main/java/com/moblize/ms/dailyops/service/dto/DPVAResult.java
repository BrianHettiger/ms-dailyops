package com.moblize.ms.dailyops.service.dto;

import com.moblize.ms.dailyops.domain.ScaledSurveyData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class DPVAResult {

    private DPVAData dpvaData;
    private Map<String, List<ScaledSurveyData>> offsetSurveyData = new HashMap<>();
}
