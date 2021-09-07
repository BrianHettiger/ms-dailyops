package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moblize.ms.dailyops.service.dto.SurveyTortuosity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "surveyTortuosityDPVA")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class SurveyTortuosityDPVA {
    @Id
    String id;
    String wellUid;
    @Embedded
    private List<SurveyTortuosity> surveyTortuosityList = new ArrayList<>();
    @CreatedDate
    LocalDateTime addedAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
}
