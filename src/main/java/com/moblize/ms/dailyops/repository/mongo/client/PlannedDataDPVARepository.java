package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.PlannedDataDpva;
import com.moblize.ms.dailyops.domain.mongo.SurveyDataDpva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannedDataDPVARepository extends MongoRepository<PlannedDataDpva, String> {

    PlannedDataDpva findFirstByWellUid(String uid);
    List<PlannedDataDpva> findByWellUidIn(List<String> uid);
    PlannedDataDpva findByWellUidAndCustomer(String uid, String customer);

}
