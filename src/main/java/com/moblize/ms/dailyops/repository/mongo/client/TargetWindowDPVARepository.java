package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetWindowDPVARepository extends MongoRepository<TargetWindowDPVA, String> {

    TargetWindowDPVA findFirstByUid(String uid);

    void deleteByUid(String uid);
}
