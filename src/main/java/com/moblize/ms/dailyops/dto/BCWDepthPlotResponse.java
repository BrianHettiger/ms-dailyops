package com.moblize.ms.dailyops.dto;

import com.moblize.ms.dailyops.domain.mongo.DepthLogResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BCWDepthPlotResponse {
    private String message;
    private String status;
    private List<DepthLogResponse> data;
}
