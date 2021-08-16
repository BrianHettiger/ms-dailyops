package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String id;
    private String wellUid;
    private String customer;
    private String wellStatus;
    private List<ScaledSurveyData> scaledSurveyData= new ArrayList<>();
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Getter
    @Setter
    public class ScaledSurveyData {
        private Double md;
        private Double vs;
        private Double tvd;
        private Double ew;
        private Double ns;
        private Double dls;
        private Double distance;
        private boolean isIn;

    }
}
