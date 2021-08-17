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
    @ProtoField(number = 1)
    String id;
    @ProtoField(number = 2)
    String wellUid;
    @ProtoField(number = 3)
    String customer;
    @ProtoField(number = 4)
    String wellStatus;
    @Embedded
    @ProtoField(number = 5, collectionImplementation = ArrayList.class)
    List<ScaledSurveyData> scaledSurveyData= new ArrayList<>();
    @CreatedDate
    @ProtoField(number = 6)
    LocalDateTime addedAt;
    @LastModifiedDate
    @ProtoField(number = 7)
    LocalDateTime updatedAt;


}
