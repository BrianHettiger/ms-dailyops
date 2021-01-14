package com.moblize.ms.dailyops.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "wells")
public class MongoWell {

    private String uid;
    private String name;
    private Boolean isHidden = false;
    private String customer;
    private String statusWell;
    private String timeZone;
    private Long completedAt;
    private String county;
    private DaysVsDepthAdjustmentDates daysVsDepthAdjustmentDates;
    private Location location;
    private Distance dist;
    private List<Rig> rigs = new ArrayList<>();

    @Getter
    @Setter
    public static class Location implements Serializable {

        private Float lng = 0.0f;

        private Float lat = 0.0f;

    }
    @Getter
    @Setter
    public static class DaysVsDepthAdjustmentDates implements Serializable {
        private Float spudDate;
        private Float suspendDate;
        private Float resumeDate;
        private Float totalDepthDate;
        private Float releaseDate;

    }


    @Getter
    @Setter
    public static class Distance implements Serializable {
        private float calculated;

    }

    @Getter
    @Setter
    public static class Rig implements Serializable {
        private String rigId;
        private Long startDate;

    }
}

