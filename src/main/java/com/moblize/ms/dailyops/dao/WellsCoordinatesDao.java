package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.WellboreStick;
import com.moblize.ms.dailyops.repository.mongo.client.WellSurveyPlannedLatLongRepository;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
@Component
public class WellsCoordinatesDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("mobMongoTemplate")
    private MongoTemplate mobMongoTemplate;

    @Autowired
    private WellSurveyPlannedLatLongRepository wellSurveyPlannedLatLongRepository;

    public List<WellSurveyPlannedLatLong> getWellCoordinates() {
        return wellSurveyPlannedLatLongRepository.findAll();
    }

    public List<WellboreStick> getWellboreStickWithROPAndCost(String script) {
        ScriptOperations scriptOps = mongoTemplate.scriptOps();
        ExecutableMongoScript echoScript = new ExecutableMongoScript(script);
        Map<String, List<WellboreStick>> map = (Map<String, List<WellboreStick>>) scriptOps.execute(echoScript, "");
        return map.get("_batch");

    }

    public List<String> getNearByWell(MongoWell well, int distance, String customer, int limit) {
        if (well == null || well.getLocation() == null || well.getLocation().getLng() == null || well.getLocation().getLat() == null) {
            return Collections.emptyList();
        }
        List<String> uidList = new ArrayList<>();

        MongoCollection<Document> collection = mobMongoTemplate.getCollection("wells");
        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
            new Document("$geoNear",
                new Document("near",
                    new Document("type", "point")
                        .append("coordinates", Arrays.asList(well.getLocation().getLng(), well.getLocation().getLat()))
                )
                    .append("spherical", true)
                    .append("distanceField", "dist.calculated")
                    .append("minDistance", 10)
                    .append("maxDistance", distance * 1609.34)
                    .append("num", 100)),
            new Document("$match", new Document("customer", customer)),
            new Document("$skip", 0),
            new Document("$limit", limit),
            new Document("$project",  new Document("_id", 0)
                .append("uid", 1))
        ));
        for (Document wellObj : output) {
            uidList.add(wellObj.get("uid").toString());
        }

        return uidList;
    }

    public WellSurveyPlannedLatLong saveWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        wellSurveyPlannedLatLong.setAddedAt(LocalDateTime.now());
        wellSurveyPlannedLatLong.setUpdatedAt(LocalDateTime.now());
        if(findWellSurveyPlannedLatLong(wellSurveyPlannedLatLong.getUid()) != null) {
            return updateWellSurveyPlannedLatLong(wellSurveyPlannedLatLong);
        } else {
            return wellSurveyPlannedLatLongRepository.save(wellSurveyPlannedLatLong);
        }
    }

    public List<WellSurveyPlannedLatLong> saveWellSurveyPlannedLatLong(List<WellSurveyPlannedLatLong> wellSurveyPlannedLatLong) {
        wellSurveyPlannedLatLong.forEach(wellSurveyPlannedLatLongRec ->{
            WellSurveyPlannedLatLong oldRec = findWellSurveyPlannedLatLong(wellSurveyPlannedLatLongRec.getUid());
            if(oldRec != null) {
                wellSurveyPlannedLatLongRec.setId(oldRec.getId());
            }
        });
        return wellSurveyPlannedLatLongRepository.saveAll((Iterable) wellSurveyPlannedLatLong);
    }

    public WellSurveyPlannedLatLong updateWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        final WellSurveyPlannedLatLong existingObj = findWellSurveyPlannedLatLong(wellSurveyPlannedLatLong.getUid());
        if(null != wellSurveyPlannedLatLong.getDrilledData() && !wellSurveyPlannedLatLong.getDrilledData().isEmpty()){
            if(null != existingObj.getDrilledData()) {
                existingObj.getDrilledData().addAll(wellSurveyPlannedLatLong.getDrilledData());
            } else {
                existingObj.setDrilledData(wellSurveyPlannedLatLong.getDrilledData());
            }
        }
        if(wellSurveyPlannedLatLong.getDistinctBHAsUsedCount() > 0){
            existingObj.setDistinctBHAsUsedCount(wellSurveyPlannedLatLong.getDistinctBHAsUsedCount());
        }
        if(null != wellSurveyPlannedLatLong.getActiveRigName() && !wellSurveyPlannedLatLong.getActiveRigName().isEmpty()){
            existingObj.setActiveRigName(wellSurveyPlannedLatLong.getActiveRigName());
            existingObj.setActiveRigStartDate(wellSurveyPlannedLatLong.getActiveRigStartDate());
        }
        existingObj.setUpdatedAt(LocalDateTime.now());
        return wellSurveyPlannedLatLongRepository.save(existingObj);
    }

    public WellSurveyPlannedLatLong findWellSurveyPlannedLatLong(String uid) {
        return wellSurveyPlannedLatLongRepository.findFirstByUid(uid);
    }

    public List<WellSurveyPlannedLatLong> findWellSurveyPlannedLatLong(List<String> uid) {
        return wellSurveyPlannedLatLongRepository.findByUidIn(uid);
    }

    public void deleteWellSurveyPlannedLatLong(String uid) {
        wellSurveyPlannedLatLongRepository.deleteByUid(uid);
    }

}
