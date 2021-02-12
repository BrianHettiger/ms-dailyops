package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "performanceCost")
@JsonIgnoreProperties(value = { "_id" })
public class PerformanceCost {

    private ObjectId _id;
    public String uid;
    public Cost cost;

    @Getter
    @Setter
    public static class Cost implements Serializable {
        public Double afe;
        public Double perFt;
        public Double perLatFt;
        public Double total;
    }
}
