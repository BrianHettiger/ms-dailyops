package com.moblize.ms.dailyops.service.dto;

import com.moblize.ms.dailyops.service.DPVAService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DonutDistanceDTO {

    double avgDistance = 0d;
    Map<String, DPVAService.DistanceDTO> data = new HashMap<>();
}
