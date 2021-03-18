package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moblize.ms.dailyops.domain.PerformanceROP;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "performanceBHA")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class PerformanceBHA {

    @Id
    private String id;
    public String uid;
    public BHACount bhaCount = new BHACount();
    public List<Bha> bha = new ArrayList<>();
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class BHACount implements Serializable {
        public Section section = new Section();
    }

    @Getter
    @Setter
    public static class Section implements Serializable {
        public int all = 0;
        public int surface = 0;
        public int intermediate = 0;
        public int curve = 0;
        public int lateral = 0;
    }

    @Getter
    @Setter
    public static class Bha implements Serializable {

        public long id;
        public String name = "";
        public float mdStart = 0f;
        public float mdEnd = 0f;
        public float footageDrilled = 0f;
        public String motorType = "";
        public List<String> sections = new ArrayList<>();
        public PerformanceROP.RopType avgRop = new PerformanceROP.RopType();
        public PerformanceROP.RopType rotatingROP = new PerformanceROP.RopType();
        public PerformanceROP.RopType slidingROP = new PerformanceROP.RopType();
        public PerformanceROP.RopType effectiveROP = new PerformanceROP.RopType();
        public PerformanceROP.RopType slidePercentage = new PerformanceROP.RopType();
        public String avgDLS = "";
        public Double buildWalkAngle = 0d;
        public Double buildWalkCompassAngle = 0d;
        public String buildWalkCompassDirection = "";


    }
}
