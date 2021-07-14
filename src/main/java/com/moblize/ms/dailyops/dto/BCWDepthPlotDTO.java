package com.moblize.ms.dailyops.dto;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BCWDepthPlotDTO {

    private String primaryWellUid;
    private List<String> offsetWellUids = new ArrayList<>();
    private int startIndex;
    private int endIndex;

}
