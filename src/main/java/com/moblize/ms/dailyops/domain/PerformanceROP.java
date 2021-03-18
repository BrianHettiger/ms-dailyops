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
    public String uid;
    public RopType avgROP = new RopType();
    public RopType rotatingROP = new RopType();
    public RopType slidingROP = new RopType();
    public RopType effectiveROP = new RopType();
    public RopType slidePercentage = new RopType();

    @Getter
    @Setter
    public static class RopType implements Serializable {
        public Section section = new Section();
    }

    @Getter
    @Setter
    public static class Section implements Serializable {
        public double all = 0d;
        public double surface = 0d;
        public double intermediate = 0d;
        public double curve = 0d;
        public double lateral = 0d;
    }
}
