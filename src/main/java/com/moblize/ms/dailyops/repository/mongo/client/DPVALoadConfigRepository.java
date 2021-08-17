package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.DPVALoadConfig;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DPVALoadConfigRepository extends MongoRepository<DPVALoadConfig, String> {

    DPVALoadConfig findFirstByCustomer(String customer);

}
