package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceBHARepository extends MongoRepository<PerformanceBHA, String> {

    PerformanceBHA findFirstByUid(String uid);

    void deleteByUid(String uid);
}
