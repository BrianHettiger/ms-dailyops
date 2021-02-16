package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.WellsCoordinatesDao;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.dto.AvgROP;
import com.moblize.ms.dailyops.dto.Section;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponse;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceROPRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WellsCoordinatesService {

    @Autowired
    private WellsCoordinatesDao wellsCoordinatesDao;

    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private PerformanceROPRepository ropRepository;

    public Collection<WellCoordinatesResponse> getWellCoordinates(String customer) {

        Map<String, WellCoordinatesResponse> latLngMap = new HashMap<>();

        List<MongoWell> mongoWell = mongoWellRepository.findAllByCustomer(customer);
        final List<PerformanceROP> ropList = ropRepository.findAll();
        final Map<String, AvgROP> ropByWellUidMap = ropList.stream()
            .collect(Collectors.toMap(
                PerformanceROP::getUid,
                WellsCoordinatesService::avgRopDomainToDto,
                (k1, k2) -> k1));
        mongoWell.forEach(well -> {
            WellCoordinatesResponse wellCoordinatesResponse = latLngMap.getOrDefault(well.getUid(), new WellCoordinatesResponse());
            wellCoordinatesResponse.setUid(well.getUid());
            wellCoordinatesResponse.setName(well.getName());
            wellCoordinatesResponse.setStatusWell(well.getStatusWell());
            if (well.getLocation() != null) {
                WellCoordinatesResponse.Location location = new WellCoordinatesResponse.Location(well.getLocation().getLng(), well.getLocation().getLat());
                wellCoordinatesResponse.setLocation(location);
            } else {
                wellCoordinatesResponse.getLocation().setLat(0f);
                wellCoordinatesResponse.getLocation().setLng(0f);
            }
            wellCoordinatesResponse.setDrilledData(Collections.emptyList());
            wellCoordinatesResponse.setPlannedData(Collections.emptyList());
            // set ROP
            wellCoordinatesResponse.setAvgROP(ropByWellUidMap.get(well.getUid()));
            latLngMap.put(well.getUid(), wellCoordinatesResponse);
        });

        HashMap<String, Float> drilledWellDepth = new HashMap<>();
        List<WellSurveyPlannedLatLong> wellSurveyDetail = wellsCoordinatesDao.getWellCoordinates();
        wellSurveyDetail.forEach(wellSurvey -> {
            WellCoordinatesResponse wellCoordinatesResponse = latLngMap.getOrDefault(wellSurvey.getUid(), new WellCoordinatesResponse());
            if (wellCoordinatesResponse.getUid() == null) {
                wellCoordinatesResponse.setUid(wellSurvey.getUid());
            }
            if (wellSurvey.getDrilledData() != null && !wellSurvey.getDrilledData().isEmpty()) {
                drilledWellDepth.put(wellSurvey.getUid(), Float.valueOf(wellSurvey.getDrilledData().get(wellSurvey.getDrilledData().size() - 1).get("depth").toString()));
                wellCoordinatesResponse.setDrilledData(wellSurvey.getDrilledData().stream().map(drill -> ((ArrayList) drill.get("coordinates")).stream().findFirst().get()).collect(Collectors.toList()));
            } else {
                wellCoordinatesResponse.setDrilledData(Collections.emptyList());
            }
            if (wellSurvey.getPlannedData() != null && !wellSurvey.getPlannedData().isEmpty() && !wellCoordinatesResponse.getStatusWell().equalsIgnoreCase("completed") && wellSurvey.getDrilledData() != null && !wellSurvey.getDrilledData().isEmpty()) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData().stream()
                    .filter(planned -> {
                        return planned != null && drilledWellDepth.get(wellSurvey.getUid()) != null && planned.get("depth") != null && planned.get("coordinates") != null ? Float.valueOf(planned.get("depth").toString()) >= drilledWellDepth.get(wellSurvey.getUid()) : false;
                    })
                    .map(drill -> ((ArrayList) drill.get("coordinates")).stream().findFirst().get()).collect(Collectors.toList()));
            } else if (wellSurvey.getPlannedData() != null && !wellSurvey.getPlannedData().isEmpty() && (wellSurvey.getDrilledData() == null || wellSurvey.getDrilledData().isEmpty())) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData().stream().map(drill -> ((ArrayList) drill.get("coordinates")).stream().findFirst().get()).collect(Collectors.toList()));
            } else {
                wellCoordinatesResponse.setPlannedData(Collections.emptyList());
            }
            // set BHAs used count
            wellCoordinatesResponse.setDistinctBHAsUsedCount(wellSurvey.getDistinctBHAsUsedCount());
            // Set Active rig name
            wellCoordinatesResponse.setActiveRigName(wellSurvey.getActiveRigName());
            latLngMap.putIfAbsent(wellSurvey.getUid(), wellCoordinatesResponse);
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
        List<String> ls = wellsCoordinatesDao.getNearByWell(mongoWellRepository.findByUid(primaryWellUID), distance, customer, limit);
        if (ls == null && ls.isEmpty()) {
            return Collections.emptyList();
        }else {
            return ls;
        }
    }

    private static AvgROP avgRopDomainToDto(final PerformanceROP ropDomain) {
        final Section section = new Section();
        if (null != ropDomain.getAvgROP() && null != ropDomain.getAvgROP().getSection()) {
            section.setAll((int)Math.round(ropDomain.getAvgROP().getSection().getAll()));
            section.setSurface((int)Math.round(ropDomain.getAvgROP().getSection().getSurface()));
            section.setIntermediate((int)Math.round(ropDomain.getAvgROP().getSection().getIntermediate()));
            section.setCurve((int)Math.round(ropDomain.getAvgROP().getSection().getCurve()));
            section.setLateral((int)Math.round(ropDomain.getAvgROP().getSection().getLateral()));
        }
        final AvgROP avgRopDto = new AvgROP();
        avgRopDto.setSection(section);
        return avgRopDto;
    }

}
