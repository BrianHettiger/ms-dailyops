package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.BCWSmoothLogData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BCWSmoothLogDataRepository extends MongoRepository<BCWSmoothLogData, String> {

    BCWSmoothLogData findBCWSmoothLogDataByBcwId(String bcwId);

    BCWSmoothLogData findBCWSmoothLogDataByBcwIdAndUid(final String bcwId, final String uid);

    Object deleteByBcwIdAndUid(final String bcwId, final String uid);
}
