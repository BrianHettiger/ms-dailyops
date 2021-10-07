package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.SurveyTortuosityDPVA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyTortuosityDPVARepository extends MongoRepository<SurveyTortuosityDPVA, String> {

    SurveyTortuosityDPVA findFirstByWellUid(String uid);
    List<SurveyTortuosityDPVA> findByWellUidIn(List<String> uid);

}
