package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceWellRepository extends MongoRepository<PerformanceWell, String> {

    PerformanceWell findFirstByUid(String uid);

    void deleteByUid(String uid);
}
