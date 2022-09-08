/*
 * Copyright (C) Moblize, Inc - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 */
package com.moblize.ms.dailyops.service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.moblize.core.model.dto.MudPropertiesDTO.MudData;
import lombok.Data;

import java.util.List;

@Data
public class MudProperties {
    @JsonProperty("wellUid")
    private String wellUid;
    @JsonProperty("mudDataList")
    private List<MudData> mudDataList;
}
