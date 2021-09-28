package com.moblize.ms.dailyops.service.dto;

import com.moblize.ms.dailyops.domain.ScaledSurveyData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SurveyPerFeetDTO {

    @ProtoField(number = 1)
    String wellUid;
    @ProtoField(number = 2)
    String customer;
    @ProtoField(number = 3)
    String wellStatus;
    @ProtoField(number = 4, collectionImplementation = ArrayList.class)
    List<ScaledSurveyData> scaledSurveyData = new ArrayList<>();
    @ProtoField(number = 5)
    Float svInPercentage = 0.0f;
    @ProtoField(number = 6)
    Float pvInPercentage = 0.0f;


}
