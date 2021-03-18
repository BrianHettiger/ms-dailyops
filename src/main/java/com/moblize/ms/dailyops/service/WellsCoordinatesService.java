package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.WellsCoordinatesDao;
import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.domain.WellSurveyPlannedLatLong;
import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import com.moblize.ms.dailyops.dto.BHA;
import com.moblize.ms.dailyops.dto.BHACount;
import com.moblize.ms.dailyops.dto.Cost;
import com.moblize.ms.dailyops.dto.ROPs;
import com.moblize.ms.dailyops.dto.Section;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponse;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponseV2;
import com.moblize.ms.dailyops.dto.WellData;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceBHARepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceCostRepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceROPRepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceWellRepository;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private PerformanceWellRepository wellRepository;

    public Map<String, List<BHA>> getWellBHAs() {

        final List<PerformanceBHA> bhaList = bhaRepository.findAll();
        return bhaList.stream().filter(obj -> null != obj.getUid())
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhasToDto,
                (k1, k2) -> k1));
    }

    public List<WellCoordinatesResponse> getWellCoordinatesV1(String customer) {
        Collection<WellCoordinatesResponseV2> v2List = getWellCoordinates(customer);
        return v2List.stream().map(v2 -> {
            WellCoordinatesResponse res = new WellCoordinatesResponse();
            res.setUid(v2.getUid());
            res.setName(v2.getName());
            res.setActiveRigName(v2.getActiveRigName());
            res.setDistinctBHAsUsedCount(v2.getDistinctBHAsUsedCount());
            if(null != v2.getLocation() ){
                res.setLocation(new WellCoordinatesResponse.Location(v2.getLocation().getLng(), v2.getLocation().getLat()));
            }
            if(null != v2.getAvgROP()) {
                WellCoordinatesResponse.ROP avgROP = new WellCoordinatesResponse.ROP();
                avgROP.setSection(new WellCoordinatesResponse.Section(v2.getAvgROP().getSection().getAll(),
                    v2.getAvgROP().getSection().getSurface(), v2.getAvgROP().getSection().getIntermediate(), v2.getAvgROP().getSection().getCurve(),
                    v2.getAvgROP().getSection().getLateral()));
                res.setAvgROP(avgROP);
            }
            if(null != v2.getCost()){
                res.setCost(v2.getCost());
            }
            res.setDrilledData(v2.getDrilledData());
            res.setPlannedData(v2.getPlannedData());
            return res;
        }).collect(Collectors.toList());
    }

    public Collection<WellCoordinatesResponseV2> getWellCoordinates(String customer) {

        Map<String, WellCoordinatesResponseV2> latLngMap = new HashMap<>();

        List<MongoWell> mongoWell = mongoWellRepository.findAllByCustomer(customer);
        final Map<String, ROPs> ropByWellUidMap = getWellROPsMap();
        final Map<String, Cost> costByWellUidMap = getWellCostMap();
        final Map<String, BHACount> bhaCountByUidMap = getWellBHACountMap();
        final Map<String, WellData> wellMap = getWellDataMap();

        mongoWell.forEach(well -> {
            WellCoordinatesResponseV2 wellCoordinatesResponse = latLngMap.getOrDefault(well.getUid(), new WellCoordinatesResponseV2());
            wellCoordinatesResponse.setUid(well.getUid());
            wellCoordinatesResponse.setName(well.getName());
            wellCoordinatesResponse.setStatusWell(well.getStatusWell());
            if (null != well.getDaysVsDepthAdjustmentDates()) {
                wellCoordinatesResponse.setSpudDate(well.getDaysVsDepthAdjustmentDates().getSpudDate());
            } else {
                wellCoordinatesResponse.setSpudDate(0f);
            }

            if (well.getLocation() != null) {
                WellCoordinatesResponseV2.Location location = new WellCoordinatesResponseV2.Location(well.getLocation().getLng(), well.getLocation().getLat());
                wellCoordinatesResponse.setLocation(location);
            } else {
                wellCoordinatesResponse.getLocation().setLat(0f);
                wellCoordinatesResponse.getLocation().setLng(0f);
            }
            wellCoordinatesResponse.setDrilledData(Collections.emptyList());
            wellCoordinatesResponse.setPlannedData(Collections.emptyList());
            // set avgROP
            wellCoordinatesResponse.setAvgROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getAvgROP());
            wellCoordinatesResponse.setSlidingROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getSlidingROP());
            wellCoordinatesResponse.setRotatingROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getRotatingROP());
            wellCoordinatesResponse.setEffectiveROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getEffectiveROP());
            wellCoordinatesResponse.setCost(costByWellUidMap.get(well.getUid()));
            wellCoordinatesResponse.setBhaCount(bhaCountByUidMap.get(well.getUid()));
            wellCoordinatesResponse.setTotalDays(wellMap.getOrDefault(well.getUid(), new WellData()).getTotalDays());
            wellCoordinatesResponse.setFootagePerDay(wellMap.getOrDefault(well.getUid(), new WellData()).getFootagePerDay());
            wellCoordinatesResponse.setSlidingPercentage(ropByWellUidMap.get(well.getUid()).getSlidingPercentage());
            wellCoordinatesResponse.setHoleSectionRange(wellMap.getOrDefault(well.getUid(), new WellData()).getHoleSectionRange());
            latLngMap.put(well.getUid(), wellCoordinatesResponse);
        });

        HashMap<String, Float> drilledWellDepth = new HashMap<>();
        List<WellSurveyPlannedLatLong> wellSurveyDetail = wellsCoordinatesDao.getWellCoordinates();
        wellSurveyDetail.forEach(wellSurvey -> {
            WellCoordinatesResponseV2 wellCoordinatesResponse = latLngMap.getOrDefault(wellSurvey.getUid(), new WellCoordinatesResponseV2());
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

    private Map<String, WellData> getWellDataMap() {
        final List<PerformanceWell> wellList = wellRepository.findAll();

        return wellList.stream()
            .collect(Collectors.toMap(
                PerformanceWell::getUid,
                WellsCoordinatesService::performanceWellToDto,
                (k1, k2) -> k1));
    }

    private Map<String, BHACount> getWellBHACountMap() {
        final List<PerformanceBHA> bhaList = bhaRepository.findAll();

        return bhaList.stream().filter(obj -> null != obj.getUid())
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhaSectionCountToDto,
                (k1, k2) -> k1));
    }

    private Map<String, Cost> getWellCostMap() {
        final List<PerformanceCost> costList = costRepository.findAll();
        return costList.stream()
            .collect(
                Collectors.toMap(
                    PerformanceCost::getUid,
                    WellsCoordinatesService::costToDto,
                    (k1, k2) -> k1));
    }

    private Map<String, ROPs> getWellROPsMap() {
        final List<PerformanceROP> ropList = ropRepository.findAll();
        return ropList.stream()
            .collect(Collectors.toMap(
                PerformanceROP::getUid,
                WellsCoordinatesService::ropDomainToDto,
                (k1, k2) -> k1));
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
    private static Double dataConvert(Double value){
        return BigDecimal.valueOf(value == null ? 0 : value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
    private static Double dataConvertTwoDecimal(Double value){
        return BigDecimal.valueOf(value == null ? 0 : value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static ROPs ropDomainToDto(final PerformanceROP ropDomain) {
        final ROPs ropDto = new ROPs();
        if (null != ropDomain.getAvgROP() && null != ropDomain.getAvgROP().getSection()) {
            final Section avgROPSection = new Section();
            avgROPSection.setAll(dataConvert(ropDomain.getAvgROP().getSection().getAll()));
            avgROPSection.setSurface(dataConvert(ropDomain.getAvgROP().getSection().getSurface()));
            avgROPSection.setIntermediate(dataConvert(ropDomain.getAvgROP().getSection().getIntermediate()));
            avgROPSection.setCurve(dataConvert(ropDomain.getAvgROP().getSection().getCurve()));
            avgROPSection.setLateral(dataConvert(ropDomain.getAvgROP().getSection().getLateral()));
            ROPs.ROP ropAvg = new ROPs.ROP();
            ropAvg.setSection(avgROPSection);
            ropDto.setAvgROP(ropAvg);
        }
        if (null != ropDomain.getSlidingROP() && null != ropDomain.getSlidingROP().getSection()) {
            final Section slidingROPSection = new Section();
            slidingROPSection.setAll(dataConvert(ropDomain.getSlidingROP().getSection().getAll()));
            slidingROPSection.setSurface(dataConvert(ropDomain.getSlidingROP().getSection().getSurface()));
            slidingROPSection.setIntermediate(dataConvert(ropDomain.getSlidingROP().getSection().getIntermediate()));
            slidingROPSection.setCurve(dataConvert(ropDomain.getSlidingROP().getSection().getCurve()));
            slidingROPSection.setLateral(dataConvert(ropDomain.getSlidingROP().getSection().getLateral()));
            ROPs.ROP slidingROP = new ROPs.ROP();
            slidingROP.setSection(slidingROPSection);
            ropDto.setSlidingROP(slidingROP);
        }
        if (null != ropDomain.getRotatingROP() && null != ropDomain.getRotatingROP().getSection()) {
            final Section rotatingROPSection = new Section();
            rotatingROPSection.setAll(dataConvert(ropDomain.getRotatingROP().getSection().getAll()));
            rotatingROPSection.setSurface(dataConvert(ropDomain.getRotatingROP().getSection().getSurface()));
            rotatingROPSection.setIntermediate(dataConvert(ropDomain.getRotatingROP().getSection().getIntermediate()));
            rotatingROPSection.setCurve(dataConvert(ropDomain.getRotatingROP().getSection().getCurve()));
            rotatingROPSection.setLateral(dataConvert(ropDomain.getRotatingROP().getSection().getLateral()));
            ROPs.ROP rotatingROP = new ROPs.ROP();
            rotatingROP.setSection(rotatingROPSection);
            ropDto.setRotatingROP(rotatingROP);
        }
        if (null != ropDomain.getEffectiveROP() && null != ropDomain.getEffectiveROP().getSection()) {
            final Section effectiveROPSection = new Section();
            effectiveROPSection.setAll(dataConvert(ropDomain.getEffectiveROP().getSection().getAll()));
            effectiveROPSection.setSurface(dataConvert(ropDomain.getEffectiveROP().getSection().getSurface()));
            effectiveROPSection.setIntermediate(dataConvert(ropDomain.getEffectiveROP().getSection().getIntermediate()));
            effectiveROPSection.setCurve(dataConvert(ropDomain.getEffectiveROP().getSection().getCurve()));
            effectiveROPSection.setLateral(dataConvert(ropDomain.getEffectiveROP().getSection().getLateral()));
            ROPs.ROP effectiveROP = new ROPs.ROP();
            effectiveROP.setSection(effectiveROPSection);
            ropDto.setEffectiveROP(effectiveROP);
        }
        if (null != ropDomain.getSlidePercentage()&& null != ropDomain.getSlidePercentage().getSection()) {
            final Section slidePercentageSection = new Section();
            slidePercentageSection.setAll(dataConvertTwoDecimal(ropDomain.getSlidePercentage().getSection().getAll()));
            slidePercentageSection.setSurface(dataConvertTwoDecimal(ropDomain.getSlidePercentage().getSection().getSurface()));
            slidePercentageSection.setIntermediate(dataConvertTwoDecimal(ropDomain.getSlidePercentage().getSection().getIntermediate()));
            slidePercentageSection.setCurve(dataConvertTwoDecimal(ropDomain.getSlidePercentage().getSection().getCurve()));
            slidePercentageSection.setLateral(dataConvertTwoDecimal(ropDomain.getSlidePercentage().getSection().getLateral()));
            ROPs.ROP sliderPercentage = new ROPs.ROP();
            sliderPercentage.setSection(slidePercentageSection);
            ropDto.setSlidingPercentage(sliderPercentage);
        }
        return ropDto;
    }

    private static Cost costToDto(final PerformanceCost domainCost) {
        final Cost cost = new Cost();
        if (null != domainCost && null != domainCost.getCost()) {
            if (null != domainCost.getCost().getAfe()) {
                cost.setAfe(dataConvert(domainCost.getCost().getAfe()));
            }
            if (null != domainCost.getCost().getPerFt()) {
                cost.setPerFt(dataConvert(domainCost.getCost().getPerFt()));
            }
            if (null != domainCost.getCost().getPerLatFt()) {
                cost.setPerLatFt(dataConvert(domainCost.getCost().getPerLatFt()));
            }
            if (null != domainCost.getCost().getTotal()) {
                cost.setTotal(dataConvert(domainCost.getCost().getTotal()));
            }
        }
        return cost;
    }

    private static List<BHA> bhasToDto(final PerformanceBHA performanceBHA) {
        List<BHA> bhaList = new ArrayList<>();
        if (null != performanceBHA.getBha() && performanceBHA.getUid() != null && !performanceBHA.getBha().isEmpty()) {
            performanceBHA.getBha().forEach(bhaMongo -> {
                BHA bha = new BHA();
                bha.setId(bhaMongo.getId());
                bha.setName(bhaMongo.getName());
                bha.setMdStart(Math.round(bhaMongo.getMdStart()));
                bha.setMdEnd(Math.round(bhaMongo.getMdEnd()));
                bha.setFootageDrilled(Math.round(bhaMongo.getFootageDrilled()));
                bha.setMotorType(bhaMongo.getMotorType());
                bha.setSections(bhaMongo.getSections());
                bha.setAvgRop(new BHA.RopType(
                    new BHA.Section(
                        dataConvert(bhaMongo.getAvgRop().getSection().getAll()),
                        dataConvert(bhaMongo.getAvgRop().getSection().getSurface()),
                        dataConvert(bhaMongo.getAvgRop().getSection().getIntermediate()),
                        dataConvert(bhaMongo.getAvgRop().getSection().getCurve()),
                        dataConvert(bhaMongo.getAvgRop().getSection().getLateral())
                    )));
                bha.setSlidingROP(new BHA.RopType(
                    new BHA.Section(
                        dataConvert(bhaMongo.getSlidingROP().getSection().getAll()),
                        dataConvert(bhaMongo.getSlidingROP().getSection().getSurface()),
                        dataConvert(bhaMongo.getSlidingROP().getSection().getIntermediate()),
                        dataConvert(bhaMongo.getSlidingROP().getSection().getCurve()),
                        dataConvert(bhaMongo.getSlidingROP().getSection().getLateral())
                    )));
                bha.setRotatingROP(new BHA.RopType(
                    new BHA.Section(
                        dataConvert(bhaMongo.getRotatingROP().getSection().getAll()),
                        dataConvert(bhaMongo.getRotatingROP().getSection().getSurface()),
                        dataConvert(bhaMongo.getRotatingROP().getSection().getIntermediate()),
                        dataConvert(bhaMongo.getRotatingROP().getSection().getCurve()),
                        dataConvert(bhaMongo.getRotatingROP().getSection().getLateral())
                    )));
                bha.setEffectiveROP(new BHA.RopType(
                    new BHA.Section(
                        dataConvert(bhaMongo.getEffectiveROP().getSection().getAll()),
                        dataConvert(bhaMongo.getEffectiveROP().getSection().getSurface()),
                        dataConvert(bhaMongo.getEffectiveROP().getSection().getIntermediate()),
                        dataConvert(bhaMongo.getEffectiveROP().getSection().getCurve()),
                        dataConvert(bhaMongo.getEffectiveROP().getSection().getLateral())
                    )));
                bha.setSlidePercentage(new BHA.RopType(
                    new BHA.Section(
                        dataConvertTwoDecimal(bhaMongo.getSlidePercentage().getSection().getAll()),
                        dataConvertTwoDecimal(bhaMongo.getSlidePercentage().getSection().getSurface()),
                        dataConvertTwoDecimal(bhaMongo.getSlidePercentage().getSection().getIntermediate()),
                        dataConvertTwoDecimal(bhaMongo.getSlidePercentage().getSection().getCurve()),
                        dataConvertTwoDecimal(bhaMongo.getSlidePercentage().getSection().getLateral())
                    )));
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
        if (null != performanceBHA.getUid()) {
            section.setAll(performanceBHA.getBhaCount().getSection().getAll());
            section.setCurve(performanceBHA.getBhaCount().getSection().getCurve());
            section.setIntermediate(performanceBHA.getBhaCount().getSection().getIntermediate());
            section.setSurface(performanceBHA.getBhaCount().getSection().getSurface());
            section.setLateral(performanceBHA.getBhaCount().getSection().getLateral());
            bhaCount.setSection(section);
        }
        return bhaCount;
    }

    private static WellData performanceWellToDto(final PerformanceWell performanceWell) {
        WellData.SectionData totalDays = new WellData.SectionData();
        totalDays.setSection(new WellData.Section(dataConvert(performanceWell.getTotalDays().getSection().getAll()),
            dataConvert(performanceWell.getTotalDays().getSection().getSurface()),
            dataConvert(performanceWell.getTotalDays().getSection().getIntermediate()),
            dataConvert(performanceWell.getTotalDays().getSection().getCurve()),
            dataConvert(performanceWell.getTotalDays().getSection().getLateral())));
        WellData.SectionData footagePerDay = new WellData.SectionData();
        footagePerDay.setSection(new WellData.Section(dataConvert(performanceWell.getFootagePerDay().getSection().getAll()),
            dataConvert(performanceWell.getFootagePerDay().getSection().getSurface()),
            dataConvert(performanceWell.getFootagePerDay().getSection().getIntermediate()),
            dataConvert(performanceWell.getFootagePerDay().getSection().getCurve()),
            dataConvert(performanceWell.getFootagePerDay().getSection().getLateral())));
        Map<String, WellData.RangeData> holeSectionRange = new HashMap<>();
        performanceWell.getHoleSectionRange().entrySet().forEach(sec -> {
            System.out.println(performanceWell.getUid()+" "+ sec.getKey()+" "+sec.getValue().getStart()+" "+ sec.getValue().getEnd()+" "+ sec.getValue().getDiff());
            holeSectionRange.put(getSectionKey(sec.getKey()), new WellData.RangeData(Math.round(sec.getValue().getStart()), Math.round(sec.getValue().getEnd()), Math.round(sec.getValue().getDiff())));
        });
        return new WellData(performanceWell.getUid(), totalDays, footagePerDay, holeSectionRange);
    }

    private static String getSectionKey(String sectionName) {
        String key = "";
        switch (sectionName) {
            case "surface":
                key = "s";
                break;
            case "curve":
                key = "c";
                break;
            case "intermediate":
                key = "i";
                break;
            case "lateral":
                key = "l";
                break;
            case "all":
                key = "a";
                break;
        }
        return key;
    }

}
