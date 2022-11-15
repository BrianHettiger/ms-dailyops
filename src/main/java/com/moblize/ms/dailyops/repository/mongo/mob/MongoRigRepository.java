package com.moblize.ms.dailyops.repository.mongo.mob;


import com.moblize.ms.dailyops.domain.MongoRig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface MongoRigRepository extends MongoRepository<MongoRig, String> {
    Stream<MongoRig> findAllByIdIn(List<String> id);
}
