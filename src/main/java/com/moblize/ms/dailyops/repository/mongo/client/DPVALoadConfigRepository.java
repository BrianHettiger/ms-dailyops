package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.DailyOpsLoadConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DPVALoadConfigRepository extends MongoRepository<DailyOpsLoadConfig, String> {

    DailyOpsLoadConfig findFirstByCustomer(String customer);

}
