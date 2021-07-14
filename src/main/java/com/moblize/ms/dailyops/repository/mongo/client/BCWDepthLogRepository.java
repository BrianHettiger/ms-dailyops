package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.BCWDepthLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BCWDepthLogRepository extends MongoRepository<BCWDepthLog, String> {

    BCWDepthLog findFirstByUid(String uid);
}
