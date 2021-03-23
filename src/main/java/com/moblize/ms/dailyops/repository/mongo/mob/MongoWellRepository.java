package com.moblize.ms.dailyops.repository.mongo.mob;

import com.moblize.ms.dailyops.domain.MongoWell;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoWellRepository extends MongoRepository<MongoWell, String> {
    public List<MongoWell> findAllByUidIn(List<String> iterable);

    public List<MongoWell> findAllByCustomer(String customer);

    public List<MongoWell> findAllByCustomerAndIsHidden(String customer, Boolean isHidden);

    MongoWell findByUid(String uid);
}
