package com.moblize.ms.dailyops.repository.mongo.client;

import com.moblize.ms.dailyops.domain.mongo.TargetWindowPerFootDPVA;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetWindowPerFootRepository extends MongoRepository<TargetWindowPerFootDPVA, String> {
    TargetWindowPerFootDPVA findFirstByWellUid(String uid);
    List<TargetWindowPerFootDPVA> findByWellUidIn(List<String> uid);
}
