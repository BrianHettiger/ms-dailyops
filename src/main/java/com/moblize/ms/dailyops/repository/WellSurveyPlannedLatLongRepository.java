package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WellSurveyPlannedLatLongRepository extends MongoRepository<WellSurveyPlannedLatLong, String> {

    public WellSurveyPlannedLatLong findByUid(String uid);
}
