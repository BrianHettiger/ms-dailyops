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
@JsonIgnoreProperties(value = {"_id"})
public class PerformanceROP {

    private ObjectId _id;
    private String uid;
    private RopType avgROP = new RopType();
    private RopType rotatingROP = new RopType();
    private RopType slidingROP = new RopType();
    private RopType effectiveROP = new RopType();
    private RopType slidePercentage = new RopType();
    private RopType footageDrilled = new RopType();

    @Getter
    @Setter
    public static class RopType implements Serializable {
        private Section section = new Section();
    }

    @Getter
    @Setter
    public static class Section implements Serializable {
        private Double all = null;
        private Double surface = null;
        private Double intermediate = null;
        private Double curve = null;
        private Double lateral = null;
    }
}
