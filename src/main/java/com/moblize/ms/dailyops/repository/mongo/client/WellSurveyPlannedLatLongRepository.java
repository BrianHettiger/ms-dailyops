package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellSurveyPlannedLatLongRepository extends MongoRepository<WellSurveyPlannedLatLong, String> {

    public WellSurveyPlannedLatLong findByUid(String uid);

    public List<WellSurveyPlannedLatLong> findByUidIn(List<String> uid);

    public void deleteByUid(String uid);




}
