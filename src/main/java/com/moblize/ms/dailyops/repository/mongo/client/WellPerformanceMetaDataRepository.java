package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.WellPerformanceMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WellPerformanceMetaDataRepository extends MongoRepository<WellPerformanceMetaData, String> {
    WellPerformanceMetaData findByWellUid(String wellUid);

    Object deleteByWellUid(String wellUid);
}
