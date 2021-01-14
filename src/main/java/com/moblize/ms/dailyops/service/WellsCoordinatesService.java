package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dto.WellCoordinatesResponse;
import com.moblize.ms.dailyops.dao.WellsCoordinatesDao;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
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
            if(well.getLocation() != null) {
                WellCoordinatesResponse.Location location = new WellCoordinatesResponse.Location(well.getLocation().getLat(),well.getLocation().getLat() );
                wellCoordinatesResponse.setLocation(location);
            } else {
                wellCoordinatesResponse.getLocation().setLat(0f);
                wellCoordinatesResponse.getLocation().setLng(0f);
            }
            latLngMap.put(well.getUid(), wellCoordinatesResponse);
        });

        List<WellSurveyPlannedLatLong> wellSurveyDetail = wellsCoordinatesDao.getWellCoordinates();
        wellSurveyDetail.forEach(wellSurvey -> {
            WellCoordinatesResponse wellCoordinatesResponse = latLngMap.getOrDefault(wellSurvey.getUid(), new WellCoordinatesResponse());
            if(wellSurvey.getDrilledData() != null) {
                wellCoordinatesResponse.setDrilledData(wellSurvey.getDrilledData());
            }
            if(wellSurvey.getPlannedData() != null) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData());
            }
            latLngMap.put(wellSurvey.getUid(), wellCoordinatesResponse);
        });



        return latLngMap.values();
    }

}
