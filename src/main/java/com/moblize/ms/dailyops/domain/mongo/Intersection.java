package com.moblize.ms.dailyops.domain.mongo;

import lombok.*;

@Getter @Setter @NoArgsConstructor
public class Intersection {
    private Double xAxis;
    private boolean isIn;

    public Intersection(final Double xAxis, final boolean isIn) {
        this.xAxis = xAxis;
        this.isIn = isIn;
    }
}
