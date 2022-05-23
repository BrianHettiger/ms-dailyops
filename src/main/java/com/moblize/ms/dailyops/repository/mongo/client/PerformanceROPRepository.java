package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.PerformanceROP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceROPRepository extends MongoRepository<PerformanceROP, String> {

    PerformanceROP findByUid(String uid);

    PerformanceROP findFirstByUid(String uid);

    void deleteByUid(String uid);
}
