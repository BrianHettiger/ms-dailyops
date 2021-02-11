package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.WellboreStick;
import com.moblize.ms.dailyops.repository.mongo.client.WellSurveyPlannedLatLongRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public List<MongoWell> getNearByWell(MongoWell well, int distance, String customer, int limit) {
        if (well == null || well.getLocation() == null || well.getLocation().getLng() == null || well.getLocation().getLat() == null) {
            return Collections.emptyList();
        }

        BasicQuery query1 = new BasicQuery("{'location':{'$near': {\n" +
            "        '$geometry':  {'type': 'Point'," +" 'coordinates': [" + well.getLocation().getLng() + ", " + well.getLocation().getLat() + " ]},\n" +
            "    query: { type: 'public' },    '$spherical': true,'$distanceField': 'dist.calculated','$minDistance': 10,'$maxDistance': "+(distance*1609.34)+" }}       \n" +
            "       ,'customer': '" + customer + "'\n" +
            "      }  \n");
        query1.limit(limit);

        return mobMongoTemplate.find(query1, MongoWell.class);
    }

    public WellSurveyPlannedLatLong saveWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        wellSurveyPlannedLatLong.setAddedAt(LocalDateTime.now());
        wellSurveyPlannedLatLong.setUpdatedAt(LocalDateTime.now());
        return wellSurveyPlannedLatLongRepository.save(wellSurveyPlannedLatLong);
    }

    public List<WellSurveyPlannedLatLong> saveWellSurveyPlannedLatLong(List<WellSurveyPlannedLatLong> wellSurveyPlannedLatLong) {
        return wellSurveyPlannedLatLongRepository.saveAll((Iterable) wellSurveyPlannedLatLong);
    }

    public WellSurveyPlannedLatLong updateWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        final WellSurveyPlannedLatLong existingObj = findWellSurveyPlannedLatLong(wellSurveyPlannedLatLong.getUid());
        if(null != wellSurveyPlannedLatLong.getDrilledData() && !wellSurveyPlannedLatLong.getDrilledData().isEmpty()){
            existingObj.getDrilledData().addAll(wellSurveyPlannedLatLong.getDrilledData());
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
        return wellSurveyPlannedLatLongRepository.findByUid(uid);
    }

    public List<WellSurveyPlannedLatLong> findWellSurveyPlannedLatLong(List<String> uid) {
        return wellSurveyPlannedLatLongRepository.findByUidIn(uid);
    }

    public void deleteWellSurveyPlannedLatLong(String uid) {
        wellSurveyPlannedLatLongRepository.deleteByUid(uid);
    }

}
