package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DPVAResult {

    private DPVAData primaryWellDPVAData;
    private List<DPVAData> offsetWellsDPVAData = new ArrayList<>();
}
