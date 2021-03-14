package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.WellsCoordinatesDao;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import com.moblize.ms.dailyops.dto.*;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceBHARepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceCostRepository;
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
    @Autowired
    private PerformanceCostRepository costRepository;
    @Autowired
    private PerformanceBHARepository bhaRepository;

    public Map<String, List<BHA>>  getWellBHAs() {

        final List<PerformanceBHA> bhaList = bhaRepository.findAll();
        return bhaList.stream()
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhasToDto,
                (k1, k2) -> k1));
    }

    public Collection<WellCoordinatesResponse> getWellCoordinates(String customer) {

        Map<String, WellCoordinatesResponse> latLngMap = new HashMap<>();

        List<MongoWell> mongoWell = mongoWellRepository.findAllByCustomer(customer);
        final List<PerformanceROP> ropList = ropRepository.findAll();
        final Map<String, ROPs> ropByWellUidMap = ropList.stream()
            .collect(Collectors.toMap(
                PerformanceROP::getUid,
                WellsCoordinatesService::ropDomainToDto,
                (k1, k2) -> k1));
        final List<PerformanceCost> costList = costRepository.findAll();
        final Map<String, Cost> costByWellUidMap = costList.stream()
            .collect(
                Collectors.toMap(
                    PerformanceCost::getUid,
                    WellsCoordinatesService::costToDto,
                    (k1, k2) -> k1));
        final List<PerformanceBHA> bhaList = bhaRepository.findAll();

        final Map<String, BHACount> bhaSectionCountByWellUidMap = bhaList.stream()
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhaSectionCountToDto,
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
            // set avgROP
            wellCoordinatesResponse.setAvgROP(ropByWellUidMap.get(well.getUid()).getAvgROP());
            wellCoordinatesResponse.setSlidingROP(ropByWellUidMap.get(well.getUid()).getSlidingROP());
            wellCoordinatesResponse.setRotatingROP(ropByWellUidMap.get(well.getUid()).getRotatingROP());
            wellCoordinatesResponse.setEffectiveROP(ropByWellUidMap.get(well.getUid()).getEffectiveROP());
            wellCoordinatesResponse.setCost(costByWellUidMap.get(well.getUid()));
            wellCoordinatesResponse.setBhaCount(bhaSectionCountByWellUidMap.get(well.getUid()));
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
        } else {
            return ls;
        }
    }

    private static ROPs ropDomainToDto(final PerformanceROP ropDomain) {
        final ROPs ropDto = new ROPs();
        if (null != ropDomain.getAvgROP() && null != ropDomain.getAvgROP().getSection()) {
            final Section avgROPSection = new Section();
            avgROPSection.setAll((int) Math.round(ropDomain.getAvgROP().getSection().getAll()));
            avgROPSection.setSurface((int) Math.round(ropDomain.getAvgROP().getSection().getSurface()));
            avgROPSection.setIntermediate((int) Math.round(ropDomain.getAvgROP().getSection().getIntermediate()));
            avgROPSection.setCurve((int) Math.round(ropDomain.getAvgROP().getSection().getCurve()));
            avgROPSection.setLateral((int) Math.round(ropDomain.getAvgROP().getSection().getLateral()));
            ROPs.ROP ropAvg = new ROPs.ROP();
            ropAvg.setSection(avgROPSection);
            ropDto.setAvgROP(ropAvg);
        }
        if (null != ropDomain.getSlidingROP() && null != ropDomain.getSlidingROP().getSection()) {
            final Section slidingROPSection = new Section();
            slidingROPSection.setAll((int) Math.round(ropDomain.getSlidingROP().getSection().getAll()));
            slidingROPSection.setSurface((int) Math.round(ropDomain.getSlidingROP().getSection().getSurface()));
            slidingROPSection.setIntermediate((int) Math.round(ropDomain.getSlidingROP().getSection().getIntermediate()));
            slidingROPSection.setCurve((int) Math.round(ropDomain.getSlidingROP().getSection().getCurve()));
            slidingROPSection.setLateral((int) Math.round(ropDomain.getSlidingROP().getSection().getLateral()));
            ROPs.ROP slidingROP = new ROPs.ROP();
            slidingROP.setSection(slidingROPSection);
            ropDto.setSlidingROP(slidingROP);
        }
        if (null != ropDomain.getRotatingROP() && null != ropDomain.getRotatingROP().getSection()) {
            final Section rotatingROPSection = new Section();
            rotatingROPSection.setAll((int) Math.round(ropDomain.getRotatingROP().getSection().getAll()));
            rotatingROPSection.setSurface((int) Math.round(ropDomain.getRotatingROP().getSection().getSurface()));
            rotatingROPSection.setIntermediate((int) Math.round(ropDomain.getRotatingROP().getSection().getIntermediate()));
            rotatingROPSection.setCurve((int) Math.round(ropDomain.getRotatingROP().getSection().getCurve()));
            rotatingROPSection.setLateral((int) Math.round(ropDomain.getRotatingROP().getSection().getLateral()));
            ROPs.ROP rotatingROP = new ROPs.ROP();
            rotatingROP.setSection(rotatingROPSection);
            ropDto.setRotatingROP(rotatingROP);
        }
        if (null != ropDomain.getEffectiveROP() && null != ropDomain.getEffectiveROP().getSection()) {
            final Section effectiveROPSection = new Section();
            effectiveROPSection.setAll((int) Math.round(ropDomain.getEffectiveROP().getSection().getAll()));
            effectiveROPSection.setSurface((int) Math.round(ropDomain.getEffectiveROP().getSection().getSurface()));
            effectiveROPSection.setIntermediate((int) Math.round(ropDomain.getEffectiveROP().getSection().getIntermediate()));
            effectiveROPSection.setCurve((int) Math.round(ropDomain.getEffectiveROP().getSection().getCurve()));
            effectiveROPSection.setLateral((int) Math.round(ropDomain.getEffectiveROP().getSection().getLateral()));
            ROPs.ROP effectiveROP = new ROPs.ROP();
            effectiveROP.setSection(effectiveROPSection);
            ropDto.setEffectiveROP(effectiveROP);
        }
        return ropDto;
    }

    private static Cost costToDto(final PerformanceCost domainCost) {
        final Cost cost = new Cost();
        if (null != domainCost && null != domainCost.getCost()) {
            if (null != domainCost.getCost().getAfe()) {
                cost.setAfe((int) Math.round(domainCost.getCost().getAfe()));
            }
            if (null != domainCost.getCost().getPerFt()) {
                cost.setPerFt((int) Math.round(domainCost.getCost().getPerFt()));
            }
            if (null != domainCost.getCost().getPerLatFt()) {
                cost.setPerLatFt((int) Math.round(domainCost.getCost().getPerLatFt()));
            }
            if (null != domainCost.getCost().getTotal()) {
                cost.setTotal((int) Math.round(domainCost.getCost().getTotal()));
            }
        }
        return cost;
    }

    private static List<BHA> bhasToDto(final PerformanceBHA performanceBHA) {
        List<BHA> bhaList = new ArrayList<>();
        if (null != performanceBHA.getBha() && !performanceBHA.getBha().isEmpty()) {
            performanceBHA.getBha().forEach(bhaMongo -> {
                BHA bha = new BHA();
                bha.setId(bhaMongo.getId());
                bha.setName(bhaMongo.getName());
                bha.setMdStart(bhaMongo.getMdStart());
                bha.setMdEnd(bhaMongo.getMdEnd());
                bha.setFootageDrilled(bhaMongo.getFootageDrilled());
                bha.setMotorType(bhaMongo.getMotorType());
                bha.setSections(bhaMongo.getSections());
                bha.setAvgRop(new BHA.RopType(
                    new BHA.Section(
                        bhaMongo.getAvgRop().getSection().getAll(),
                        bhaMongo.getAvgRop().getSection().getSurface(),
                        bhaMongo.getAvgRop().getSection().getIntermediate(),
                        bhaMongo.getAvgRop().getSection().getCurve(),
                        bhaMongo.getAvgRop().getSection().getLateral()
                    )));
                bha.setSlidingROP(new BHA.RopType(
                    new BHA.Section(
                        bhaMongo.getSlidingROP().getSection().getAll(),
                        bhaMongo.getSlidingROP().getSection().getSurface(),
                        bhaMongo.getSlidingROP().getSection().getIntermediate(),
                        bhaMongo.getSlidingROP().getSection().getCurve(),
                        bhaMongo.getSlidingROP().getSection().getLateral()
                    )));
                bha.setRotatingROP(new BHA.RopType(
                    new BHA.Section(
                        bhaMongo.getRotatingROP().getSection().getAll(),
                        bhaMongo.getRotatingROP().getSection().getSurface(),
                        bhaMongo.getRotatingROP().getSection().getIntermediate(),
                        bhaMongo.getRotatingROP().getSection().getCurve(),
                        bhaMongo.getRotatingROP().getSection().getLateral()
                    )));
                bha.setEffectiveROP(new BHA.RopType(
                    new BHA.Section(
                        bhaMongo.getEffectiveROP().getSection().getAll(),
                        bhaMongo.getEffectiveROP().getSection().getSurface(),
                        bhaMongo.getEffectiveROP().getSection().getIntermediate(),
                        bhaMongo.getEffectiveROP().getSection().getCurve(),
                        bhaMongo.getEffectiveROP().getSection().getLateral()
                    )));
                bha.setSlidePercentage(bhaMongo.getSlidePercentage());
                bha.setAvgDLS(bhaMongo.getAvgDLS());
                bha.setBuildWalkAngle(bhaMongo.getBuildWalkAngle());
                bha.setBuildWalkCompassAngle(bhaMongo.getBuildWalkCompassAngle());
                bha.setBuildWalkCompassDirection(bhaMongo.getBuildWalkCompassDirection());
                bhaList.add(bha);

            });
        }

        return bhaList;
    }


    private static BHACount bhaSectionCountToDto(final PerformanceBHA performanceBHA) {
        BHACount bhaCount = new BHACount();
        BHACount.Section section = new BHACount.Section();
        section.setAll(performanceBHA.getBhaCount().getSection().getAll());
        section.setCurve(performanceBHA.getBhaCount().getSection().getCurve());
        section.setIntermediate(performanceBHA.getBhaCount().getSection().getIntermediate());
        section.setSurface(performanceBHA.getBhaCount().getSection().getSurface());
        section.setLateral(performanceBHA.getBhaCount().getSection().getLateral());
        bhaCount.setSection(section);

        return bhaCount;
    }

}
