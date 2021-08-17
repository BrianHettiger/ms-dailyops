package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.SurveyDataDpva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyDataDPVARepository extends MongoRepository<SurveyDataDpva, String> {

    SurveyDataDpva findFirstByWellUid(String uid);
    List<SurveyDataDpva> findByWellUidIn(List<String> uid);

}
