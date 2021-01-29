package com.moblize.ms.dailyops.domain;

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
@Document(collection = "performanceROP")
@JsonIgnoreProperties(value = { "_id" })
public class PerformanceROP {

    private ObjectId _id;
    public String uid;
    public AvgROP avgROP;

    @Getter
    @Setter
    public static class AvgROP implements Serializable {
        public Section section;
    }

    @Getter
    @Setter
    public static class Section implements Serializable {
        public Double all;
        public Double surface;
        public Double intermediate;
        public Double curve;
        public Double lateral;
    }
}
