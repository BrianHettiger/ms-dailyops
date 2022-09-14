package com.moblize.ms.dailyops.repository.mongo.mob;


import com.moblize.ms.dailyops.domain.MongoRig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRigRepository extends MongoRepository<MongoRig, String> {

}
