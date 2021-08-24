package com.moblize.ms.dailyops.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DPVARequestDTO {
    private String primaryWell;
    private List<String> offsetWell =  new ArrayList<>();
}
