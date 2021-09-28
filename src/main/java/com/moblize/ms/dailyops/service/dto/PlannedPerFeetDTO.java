package com.moblize.ms.dailyops.service.dto;

import com.moblize.ms.dailyops.domain.ScaledPlannedData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class PlannedPerFeetDTO {

    @ProtoField(number = 1)
    String wellUid;
    @ProtoField(number = 2)
    String customer;
    @ProtoField(number = 3)
    String wellStatus;
    @ProtoField(number = 4, collectionImplementation = ArrayList.class)
    List<ScaledPlannedData> scaledPlannedData = new ArrayList<>();


}
