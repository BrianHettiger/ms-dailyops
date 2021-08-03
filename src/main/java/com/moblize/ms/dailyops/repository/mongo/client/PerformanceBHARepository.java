package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceBHARepository extends MongoRepository<PerformanceBHA, String> {

    PerformanceBHA findFirstByUid(String uid);
    List<PerformanceBHA> findByUid(String uid);

    void deleteByUid(String uid);
}
