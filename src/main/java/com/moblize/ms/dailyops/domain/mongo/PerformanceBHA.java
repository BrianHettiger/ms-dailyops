package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    public BHACount bhaCount;
    public List<Bha> bha = null;
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class BHACount implements Serializable {
        public Section section;
    }

    @Getter
    @Setter
    public static class Section implements Serializable {
        public int all;
        public int surface;
        public int intermediate;
        public int curve;
        public int lateral;
    }

    @Getter
    @Setter
    public static class Bha implements Serializable {

        public long id;
        public String name;
        public float mdStart;
        public float mdEnd;
        public float footageDrilled;
        public String motorType;
        public Double avgROP;
        public Double slidingROP;
        public Double rotatingROP;
        public Double effectiveROP;
        public Double slidePercentage;
        public String avgDLS;
        public Double buildWalkAngle;
        public Double buildWalkCompassAngle;
        public String buildWalkCompassDirection;


    }
}
