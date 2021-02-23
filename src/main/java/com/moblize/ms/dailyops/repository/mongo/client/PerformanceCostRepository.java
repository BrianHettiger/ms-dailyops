package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceCostRepository extends MongoRepository<PerformanceCost, String> {

    PerformanceCost findFirstByUid(String uid);

    void deleteByUid(String uid);
}
