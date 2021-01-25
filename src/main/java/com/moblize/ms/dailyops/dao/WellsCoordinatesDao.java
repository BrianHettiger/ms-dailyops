package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.WellboreStick;
import com.moblize.ms.dailyops.repository.mongo.client.WellSurveyPlannedLatLongRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    MongoTemplate mongoTemplate;

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

    public WellSurveyPlannedLatLong saveWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        return wellSurveyPlannedLatLongRepository.save(wellSurveyPlannedLatLong);
    }

    public List<WellSurveyPlannedLatLong> saveWellSurveyPlannedLatLong(List<WellSurveyPlannedLatLong> wellSurveyPlannedLatLong) {
        return wellSurveyPlannedLatLongRepository.saveAll((Iterable) wellSurveyPlannedLatLong);
    }

    public WellSurveyPlannedLatLong updateWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        WellSurveyPlannedLatLong dbObj = findWellSurveyPlannedLatLong(wellSurveyPlannedLatLong.getUid());
        dbObj.getDrilledData().addAll(wellSurveyPlannedLatLong.getDrilledData());
        return wellSurveyPlannedLatLongRepository.save(dbObj);
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
