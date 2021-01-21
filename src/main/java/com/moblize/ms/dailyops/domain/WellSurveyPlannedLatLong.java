package com.moblize.ms.dailyops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "wellSurveyPlannedLatLong")
@JsonIgnoreProperties(value = { "_id" })
public class WellSurveyPlannedLatLong {


    ObjectId _id;
    @NotNull
    private String uid;
    private List<Double[]> drilledData;
//    @NotNull
//    @NotEmpty
    private List<Double[]> plannedData;


}
