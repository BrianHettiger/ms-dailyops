package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dto.WellCoordinatesResponse;
import com.moblize.ms.dailyops.dao.WellsCoordinatesDao;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.WellboreStick;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WellsCoordinatesService {

    @Autowired
    private WellsCoordinatesDao wellsCoordinatesDao;

    @Autowired
    private MongoWellRepository mongoWellRepository;

    public Collection<WellCoordinatesResponse> getWellCoordinates(String customer) {

        Map<String, WellCoordinatesResponse> latLngMap = new HashMap<>();

        List<MongoWell> mongoWell = mongoWellRepository.findAllByCustomer(customer);
        mongoWell.forEach(well -> {
            WellCoordinatesResponse wellCoordinatesResponse = latLngMap.getOrDefault(well.getUid(), new WellCoordinatesResponse());
            wellCoordinatesResponse.setUid(well.getUid());
            wellCoordinatesResponse.setName(well.getName());
            if (well.getLocation() != null) {
                WellCoordinatesResponse.Location location = new WellCoordinatesResponse.Location(well.getLocation().getLat(), well.getLocation().getLat());
                wellCoordinatesResponse.setLocation(location);
            } else {
                wellCoordinatesResponse.getLocation().setLat(0f);
                wellCoordinatesResponse.getLocation().setLng(0f);
            }
            wellCoordinatesResponse.setDrilledData(Collections.emptyList());
            wellCoordinatesResponse.setPlannedData(Collections.emptyList());

            latLngMap.put(well.getUid(), wellCoordinatesResponse);
        });

       /*List<WellboreStick> latLongList = wellsCoordinatesDao.getWellboreStickWithROPAndCost(loadScript());
        latLongList.forEach(wellSurvey ->{
            WellCoordinatesResponse wellCoordinatesResponse = latLngMap.getOrDefault(wellSurvey.getUid(), new WellCoordinatesResponse());
            if (wellCoordinatesResponse.getUid() == null) {
                wellCoordinatesResponse.setUid(wellSurvey.getUid());
            }
            if (wellSurvey.getDrilledData() != null) {
                wellCoordinatesResponse.setDrilledData(wellSurvey.getDrilledData());
            }
            if (wellSurvey.getPlannedData() != null) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData());
            }
            if(wellSurvey.getAvgROP() != null){
                wellCoordinatesResponse.setAvgROP(wellSurvey.getAvgROP());
            }
            if(wellSurvey.getCost() != null){
                wellCoordinatesResponse.setCost(wellSurvey.getCost());
            }
            latLngMap.put(wellSurvey.getUid(), wellCoordinatesResponse);

        });*/
        HashMap<String, Float> drilledWellDepth = new HashMap<>();
        List<WellSurveyPlannedLatLong> wellSurveyDetail = wellsCoordinatesDao.getWellCoordinates();
        wellSurveyDetail.forEach(wellSurvey -> {
            WellCoordinatesResponse wellCoordinatesResponse = latLngMap.getOrDefault(wellSurvey.getUid(), new WellCoordinatesResponse());
            if (wellCoordinatesResponse.getUid() == null) {
                wellCoordinatesResponse.setUid(wellSurvey.getUid());
            }
            if (wellSurvey.getDrilledData() != null) {
                drilledWellDepth.put(wellSurvey.getUid(), Float.valueOf(wellSurvey.getDrilledData().get(wellSurvey.getDrilledData().size() - 1).get("depth").toString()));
                wellCoordinatesResponse.setDrilledData(wellSurvey.getDrilledData().stream().map(drill -> drill.get("coordinates")).collect(Collectors.toList()));
            } else {
                wellCoordinatesResponse.setDrilledData(Collections.emptyList());
            }
            if (wellSurvey.getPlannedData() != null && !wellSurvey.getPlannedData().isEmpty()) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData().stream()
                    .filter(planned -> Float.valueOf(planned.get("depth").toString()) >= drilledWellDepth.get(wellSurvey.getUid()))
                    .map(drill -> drill.get("coordinates")).collect(Collectors.toList()));
            } else {
                wellCoordinatesResponse.setPlannedData(Collections.emptyList());
            }
            latLngMap.put(wellSurvey.getUid(), wellCoordinatesResponse);
        });


        return latLngMap.values();
    }

    public String loadScript() {
        StringBuffer sb = new StringBuffer();
        try {
            File file = ResourceUtils.getFile("classpath:mongoscript\\wellboreStickWithROPAndCost");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                //process the line
                sb.append(line);
                // System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            log.error("Error while load perfomacescript", e);
        }
        return sb.toString();
    }

    public WellSurveyPlannedLatLong saveWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        return wellsCoordinatesDao.saveWellSurveyPlannedLatLong(wellSurveyPlannedLatLong);
    }

    public List<WellSurveyPlannedLatLong> saveWellSurveyPlannedLatLong(List<WellSurveyPlannedLatLong> wellSurveyPlannedLatLong) {
        return wellsCoordinatesDao.saveWellSurveyPlannedLatLong(wellSurveyPlannedLatLong);
    }

    public WellSurveyPlannedLatLong updateWellSurveyPlannedLatLong(WellSurveyPlannedLatLong wellSurveyPlannedLatLong) {
        return wellsCoordinatesDao.updateWellSurveyPlannedLatLong(wellSurveyPlannedLatLong);
    }

    public WellSurveyPlannedLatLong findWellSurveyPlannedLatLong(String uid) {
        return wellsCoordinatesDao.findWellSurveyPlannedLatLong(uid);
    }

    public List<WellSurveyPlannedLatLong> findWellSurveyPlannedLatLong(List<String> uid) {
        return wellsCoordinatesDao.findWellSurveyPlannedLatLong(uid);
    }

    public void deleteWellSurveyPlannedLatLong(String uid) {
        wellsCoordinatesDao.deleteWellSurveyPlannedLatLong(uid);
    }

    public List<String> getNearByWell(String primaryWellUID, int distance, String customer, int limit) {
        List<MongoWell> ls = wellsCoordinatesDao.getNearByWell(mongoWellRepository.findByUid(primaryWellUID), distance, customer, limit);
        if (ls != null && !ls.isEmpty()) {
            return ls.stream().map(well -> well.getUid()).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
