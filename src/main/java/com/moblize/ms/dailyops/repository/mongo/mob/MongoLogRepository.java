package com.moblize.ms.dailyops.repository.mongo.mob;

import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoLogRepository extends MongoRepository<MongoLog, String> {
    public List<MongoLog> findByUidWell(String uidWell);
    public List<MongoLog> findByIndexType(String type);
    public MongoLog findFirstByUidWellAndIndexType(String uidWell, String type);
    public MongoLog findFirstByUidWellAndUidWellboreAndIndexType(String uidWell, String uidWellbore, String logType);
    public MongoLog findMongoLogById(String id);
}
