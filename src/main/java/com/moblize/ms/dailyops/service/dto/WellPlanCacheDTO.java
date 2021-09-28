package com.moblize.ms.dailyops.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WellPlanCacheDTO {

    @ProtoField(number = 1)
    List<WellPlan> wellPlanList;
}
