package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;

public class WellUpdateEvent {
    private MongoWell mongoWell;
    WellUpdateEvent(MongoWell mongoWell) {
        this.mongoWell = mongoWell;
    }

}
