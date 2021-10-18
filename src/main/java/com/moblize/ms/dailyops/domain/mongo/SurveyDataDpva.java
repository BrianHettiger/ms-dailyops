package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moblize.ms.dailyops.domain.ScaledSurveyData;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embedded;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "surveyDataDPVA")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class SurveyDataDpva {
    @Id
    String id;
    String wellUid;
    String customer;
    String wellStatus;
    @Embedded
    List<ScaledSurveyData> scaledSurveyData= new ArrayList<>();
    private Float svInPercentage = 0.0f;
    private Float pvInPercentage = 0.0f;
    @CreatedDate
    LocalDateTime addedAt;
    @LastModifiedDate
    LocalDateTime updatedAt;


}
