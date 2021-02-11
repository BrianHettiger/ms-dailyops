package com.moblize.ms.dailyops.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "wellSurveyPlannedLatLong")
public class WellSurveyPlannedLatLong {
    private String _id;
    @NotNull
    private String uid;
    private Integer distinctBHAsUsedCount = 0;
    private String activeRigName;
    private Long activeRigStartDate;
    private List<Map<String,Object>> drilledData;
    private List<Map<String,Object>> plannedData;
    @JsonIgnore
    private LocalDateTime addedAt;
    @JsonIgnore
    private LocalDateTime updatedAt;
}
