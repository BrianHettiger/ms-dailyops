package com.moblize.ms.dailyops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "wellSurveyPlannedLatLong")
@JsonIgnoreProperties(value = { "_id" })
public class WellSurveyPlannedLatLong {

    @Id
    private ObjectId id;
    @NotNull
    private String uid;
    private Integer distinctBHAsUsedCount = 0;
    private String activeRigName;
    private Long activeRigStartDate;
    private List<Map<String,Object>> drilledData;
    private List<Map<String,Object>> plannedData;
}
