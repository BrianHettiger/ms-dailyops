package com.moblize.ms.dailyops.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.dao.WellsCoordinatesDao;
import com.moblize.ms.dailyops.domain.*;
import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import com.moblize.ms.dailyops.dto.*;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceBHARepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceCostRepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceROPRepository;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceWellRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoRigRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import com.moblize.ms.dailyops.security.jwt.TokenProvider;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WellsCoordinatesService {

    @Value("${CODE}")
    String COMPANY_NAME;

    @Autowired
    private WellsCoordinatesDao wellsCoordinatesDao;
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    private MongoRigRepository mongoRigRepository;
    @Autowired
    private PerformanceROPRepository ropRepository;
    @Autowired
    private PerformanceCostRepository costRepository;
    @Autowired
    private PerformanceBHARepository bhaRepository;
    @Autowired
    private PerformanceWellRepository wellRepository;
    @Autowired
    private RestClientService restClientService;
    @Autowired
    private MobMongoQueryService mobMongoQueryService;
    @Autowired
    private KpiDashboardClient kpiDashboardClient;
    @Autowired
    @Lazy
    private CacheService cacheService;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    TokenProvider tokenProvider;
    public Map<String, List<BHA>> getWellBHAs(String wellUid) {

        List<PerformanceBHA> bhaList;
        if(wellUid != null) {
            bhaList = bhaRepository.findByUid(wellUid);
        } else {
            bhaList = bhaRepository.findAll();
        }
        return bhaList.stream().filter(obj -> null != obj.getUid())
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhasToDto,
                (k1, k2) -> k1));
    }

    public List<WellCoordinatesResponse> getWellCoordinatesV1(String customer, String token) {
        Collection<WellCoordinatesResponseV2> v2List = getWellCoordinates(customer, token);
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

    public Collection<WellCoordinatesResponseV2> getWellCoordinates(MongoWell well) {
        Map<String, WellCoordinatesResponseV2> latLngMap = new HashMap<>();
        final Map<String, ROPs> ropByWellUidMap = getWellROPsMap(well);
        final Map<String, Cost> costByWellUidMap = getWellCostMap(well);
        final PerformanceBHA bha = bhaRepository.findFirstByUid(well.getUid());
        final Map<String, BHACount> bhaCountByUidMap = getWellBHACountMap(bha);
        final Map<String, BHAHoleSize> bhaHoleSizeByUidMap = getWellBHAHoleSizeMap(bha);
        final Map<String, WellData> wellMap = getWellDataMap(well);
        log.info("bhaCountByUidMap:{}", bhaCountByUidMap);
        populateForWell(
            well,
            ropByWellUidMap,
            costByWellUidMap,
            bhaCountByUidMap,
            wellMap,
            latLngMap,
            bhaHoleSizeByUidMap
        );
        HashMap<String, Float> drilledWellDepth = new HashMap<>();
        WellSurveyPlannedLatLong wellSurvey = wellsCoordinatesDao.findWellSurveyPlannedLatLong(well.getUid());
        if(wellSurvey != null) {
            populateForSurvey(
                wellSurvey,
                drilledWellDepth,
                latLngMap
            );
            latLngMap.get(well.getUid()).setProtoData();
        }
        cacheService.getWellCoordinatesCache()
            .put(well.getUid(),latLngMap.get(well.getUid()));
        return latLngMap.values();
    }

    public Map<String,List<Last4WellsResponse>> getLast4Wells(List<String> rigIds, String token, String customer,String primaryWellUid){
        long startTime = System.currentTimeMillis();
        Map<String,List<Last4WellsResponse>> rigWellsMap= new HashMap<>();
        long start1 = System.currentTimeMillis();
        final Map<String, ROPs> wellROPsMap = getWellROPsMap();
        final Map<String, WellData> wellMap = getWellDataMap();
        MongoWell primaryWell = mongoWellRepository.findByUid(primaryWellUid);
        List<MongoWell> mongoWells = mongoWellRepository.findAllByCustomer(customer);
        List<MongoRig> allRigsById = (List<MongoRig>) mongoRigRepository.findAllById(rigIds);
        Map<String, MongoRig> mongoRigMap = allRigsById.stream().collect(Collectors.toMap(MongoRig::getId, Function.identity()));
        log.error("Time taken to calculate common data :{}", System.currentTimeMillis()-start1);
        for (String rigId:
            rigIds) {

        List<MongoWell> rigWells = new ArrayList<>();
        boolean isPrimaryWellInRig=false;

        for (Rig rig:
            primaryWell.getRigs()) {
            if(rigId.equals(rig.getRigId())){
                rigWells.add(primaryWell);
                isPrimaryWellInRig=true;
                break;
            }
        }

        if(mongoWells!=null) {
//            mongoWells.stream().forEach(well->{
//                log.error(well.getRigs().get(0).getRigId());
//            });
            rigWells= mongoWells.stream().filter(well -> {
                if(!("completed".equals(well.getStatusWell())))
                    return false;
                else {
                      boolean useWell=false;
                    for (Rig rig:well.getRigs()
                         ) {
                        if(rigId.equals(rig.getRigId()) && (!primaryWellUid.equals(well.getUid()))){
                            useWell=true;
                            break;
                        }
                   }
                   return useWell;
                }
            }).collect(Collectors.toList());
        }
        rigWells.sort(new Comparator<MongoWell>() {
            @Override
            public int compare(MongoWell o1, MongoWell o2) {
                if(o1.getDaysVsDepthAdjustmentDates()==null && o2.getDaysVsDepthAdjustmentDates()==null)
                    return 0;
                else if(o1.getDaysVsDepthAdjustmentDates()==null && o2.getDaysVsDepthAdjustmentDates()!=null)
                    return 1;
                else if(o2.getDaysVsDepthAdjustmentDates()==null && o1.getDaysVsDepthAdjustmentDates()!=null)
                    return -1;
                else if(o1.getDaysVsDepthAdjustmentDates().getReleaseDate()==null && o2.getDaysVsDepthAdjustmentDates().getReleaseDate()==null)
                    return 0;
                else if(o1.getDaysVsDepthAdjustmentDates().getReleaseDate()==null && o2.getDaysVsDepthAdjustmentDates().getReleaseDate()!=null)
                    return 1;
                else if(o2.getDaysVsDepthAdjustmentDates().getReleaseDate()==null && o1.getDaysVsDepthAdjustmentDates().getReleaseDate()!=null)
                    return -1;
                return o2.getDaysVsDepthAdjustmentDates().getReleaseDate().compareTo(o1.getDaysVsDepthAdjustmentDates().getReleaseDate());
            }
        });

        int numWellsToSelect=4;
        if(isPrimaryWellInRig)
            numWellsToSelect=3;

        if(rigWells.size()>numWellsToSelect){
            for (MongoWell rigWell:rigWells
                 ) {
                log.error(rigWell.getName()+", rigdate ="+(rigWell.getDaysVsDepthAdjustmentDates()!=null?rigWell.getDaysVsDepthAdjustmentDates().getReleaseDate():"Null Date"));
            }
            rigWells= rigWells.subList(0,numWellsToSelect);
        }

     Collections.reverse(rigWells);

        if(isPrimaryWellInRig)
            rigWells.add(rigWells.size(),primaryWell);

        if(rigWells!=null && rigWells.size()>0){
            long start = System.currentTimeMillis();
            List<Last4WellsResponse> last4Wells = rigWells.stream().map(well -> populateLast4WellsData(well,wellROPsMap,wellMap,mongoRigMap.get(rigId))).collect(Collectors.toList());
            rigWellsMap.put(rigId,last4Wells);
            log.error("Time taken to populate populate: {}", System.currentTimeMillis()-start);
        }else{
            rigWellsMap.put(rigId,new ArrayList<Last4WellsResponse>());
            }
        }
        log.error("getTop4WellsByRig took : {}s for well : {}", System.currentTimeMillis()-startTime, primaryWellUid);
        return rigWellsMap;
    }

    private Last4WellsResponse populateLast4WellsData(MongoWell well, Map<String, ROPs> wellROPsMap, Map<String, WellData> wellMap, MongoRig mongoRig){
    //log.error("Rig Release time for "+well.getUid()+" = "+well.getDaysVsDepthAdjustmentDates().getReleaseDate().toString());
        try {
            Last4WellsResponse last4WellsResponse =  new Last4WellsResponse();
            last4WellsResponse.setUid(well.getUid());
            last4WellsResponse.setName(well.getName());
            if (null != well.getDaysVsDepthAdjustmentDates()) {
                last4WellsResponse.setSpudDate(well.getDaysVsDepthAdjustmentDates().getSpudDate());
                last4WellsResponse.setReleaseDate(well.getDaysVsDepthAdjustmentDates().getReleaseDate());
            } else {
                last4WellsResponse.setSpudDate(0f);
                last4WellsResponse.setReleaseDate(0f);
            }

            // set avgROP
            ROPs roPs = wellROPsMap.get(well.getUid());
            last4WellsResponse.setAvgROP(wellROPsMap.getOrDefault(well.getUid(), new ROPs()).getAvgROP());
            last4WellsResponse.setSlidingROP(wellROPsMap.getOrDefault(well.getUid(), new ROPs()).getSlidingROP());
            last4WellsResponse.setRotatingROP(wellROPsMap.getOrDefault(well.getUid(), new ROPs()).getRotatingROP());
            last4WellsResponse.setEffectiveROP(wellROPsMap.getOrDefault(well.getUid(), new ROPs()).getEffectiveROP());
            last4WellsResponse.setTotalDays(wellMap.getOrDefault(well.getUid(), new WellData()).getTotalDays());
            last4WellsResponse.setFootagePerDay(wellMap.getOrDefault(well.getUid(), new WellData()).getFootagePerDay());
            last4WellsResponse.setSlidingPercentage(wellROPsMap.getOrDefault(well.getUid(), new ROPs()).getSlidingPercentage());
            last4WellsResponse.setFootageDrilled(wellROPsMap.getOrDefault(well.getUid(), new ROPs()).getFootageDrilled());
            if(wellMap.get(well.getUid()) != null) {
                last4WellsResponse.setHoleSectionRange(wellMap.get(well.getUid()).getHoleSectionRange());
            }
            last4WellsResponse.setAvgDLSBySection(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgDLSBySection());
            last4WellsResponse.setAvgMYBySection(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgMYBySection());
            last4WellsResponse.setAvgDirectionAngle(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgDirectionAngle());
            last4WellsResponse.setAvgDirection(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgDirection());
            last4WellsResponse.setSectionConnections(kpiDashboardClient.getSectionConnections(well.getUid()));
            Map<String, Map<String, Map<HoleSection.HoleSectionType, Float>>> trippingData = kpiDashboardClient.getKpiExtractionByWellId(well.getUid());
            Map<String, Map<HoleSection.HoleSectionType, Float>> trippingDataForWell = trippingData.get(well.getUid());
            Map<String, Map<String, Float>> wellTrip= new HashMap<>();
            trippingDataForWell.forEach((tripType, tripValue) -> {
                Map<String, Float> data = new HashMap<>();
                tripValue.forEach((key, value) -> data.put((key.name().toLowerCase(Locale.ROOT).substring(0, 1)), value));
                wellTrip.put(tripType, data);
            });
            last4WellsResponse.setTrippingData(wellTrip);
            last4WellsResponse.setRigName(mongoRig.getName());
            return last4WellsResponse;
        }catch (Exception exp){
            log.error("Error occurred while processing well: {}", well.getUid(), exp);
        }
        return null;
    }


    public Collection<WellCoordinatesResponseV2> getWellCoordinates(String customer, String token) {
        List<MongoWell> mongoWells = null;
        mongoWells = mongoWellRepository.findAllByCustomer(customer);
        RemoteCache<String, WellCoordinatesResponseV2> remoteCache = cacheService.getWellCoordinatesCache();
        Map<String, WellCoordinatesResponseV2> latLngMap = new HashMap<>();

        if(token != null) {
            Claims claims = tokenProvider.getTokenClaims(token);
            String email = (String) claims.get("email");
            List<String> restrictedUsers = Arrays.asList(new String[]{"luis_alzate_rodeojv@oxy.com", "jose_mondragon_rodeojv@oxy.com", "davidc.morales2@gmail.com"});
            if(email != null && restrictedUsers.contains(email.toLowerCase(Locale.ROOT))) {
                List<String> restrictedRigs = Arrays.asList(new String[]{"H-P 434", "H-P 480", "H-P 427", "H-P 617"});
                List<String> restrictedRigIds = mobMongoQueryService.getRigIdsByName(restrictedRigs);
                mongoWells = mobMongoQueryService.getWellsByRigIds(restrictedRigIds);
            } else if (email != null && email.toLowerCase().contains("moblize")) {
                mongoWells = mongoWellRepository.findAllByCustomer(customer);
            }
        }
        if(mongoWells == null){
            mongoWells = mongoWellRepository.findAllByCustomerAndIsHidden(customer, false);
        }
        if(!remoteCache.isEmpty()) {
            mongoWells.forEach(mongoWell -> {
                WellCoordinatesResponseV2 value = remoteCache.get(mongoWell.getUid());
                if(value != null){
                    value.setEntries();
                    latLngMap.put(value.getUid(), value);
                }
            });
            if(latLngMap.isEmpty()) {
                mongoWells.forEach(mongoWell -> {
                    WellCoordinatesResponseV2 value = remoteCache.get(mongoWell.getUid());
                    if(value != null) {
                        value.setEntries();
                        latLngMap.put(value.getUid(), value);
                    }
                });
            }
            return latLngMap.values();
        }

        final Map<String, ROPs> ropByWellUidMap = getWellROPsMap();
        final Map<String, Cost> costByWellUidMap = getWellCostMap();
        final List<PerformanceBHA> bhaList = bhaRepository.findAll();
        final Map<String, BHACount> bhaCountByUidMap = getWellBHACountMap(bhaList);
        final Map<String, BHAHoleSize> bhaHoleSizeByUidMap = getWellBHAHoleSizeMap(bhaList);
        final Map<String, WellData> wellMap = getWellDataMap();

        mongoWells.forEach(well -> populateForWell(
            well,
            ropByWellUidMap,
            costByWellUidMap,
            bhaCountByUidMap,
            wellMap,
            latLngMap,
            bhaHoleSizeByUidMap
        ));

        HashMap<String, Float> drilledWellDepth = new HashMap<>();
        List<WellSurveyPlannedLatLong> wellSurveyDetail = wellsCoordinatesDao.getWellCoordinates();
        wellSurveyDetail.forEach(wellSurvey -> populateForSurvey(
            wellSurvey,
            drilledWellDepth,
            latLngMap
        ));
        if(remoteCache.isEmpty()) {
            for (WellCoordinatesResponseV2 value : latLngMap.values()) {
                value.setProtoData();
            }
            remoteCache.putAll(latLngMap);
        }
        return latLngMap.values();
    }
    private void populateForSurvey(
        WellSurveyPlannedLatLong wellSurvey,
        HashMap<String, Float> drilledWellDepth,
        Map<String, WellCoordinatesResponseV2> latLngMap
    ) {
        try {
            WellCoordinatesResponseV2 wellCoordinatesResponse = latLngMap.get(wellSurvey.getUid());
            if(null == wellCoordinatesResponse) {
                return;
            }
            if (wellCoordinatesResponse.getUid() == null) {
                wellCoordinatesResponse.setUid(wellSurvey.getUid());
            }
            if (wellSurvey.getDrilledData() != null && !wellSurvey.getDrilledData().isEmpty()) {

                drilledWellDepth.put(wellSurvey.getUid(), Float.valueOf(wellSurvey.getDrilledData().get(wellSurvey.getDrilledData().size() - 1).get("depth").toString()));
                wellCoordinatesResponse.setDrilledData(wellSurvey.getDrilledData().stream().map(drill ->
                    (List<Double>) ((List) drill.get("coordinates")).stream().findFirst().get()
                ).collect(Collectors.toList()));
            } else {
                wellCoordinatesResponse.setDrilledData(Collections.emptyList());
            }
            if (wellSurvey.getPlannedData() != null && !wellSurvey.getPlannedData().isEmpty() && !wellCoordinatesResponse.getStatusWell().equalsIgnoreCase("completed") && wellSurvey.getDrilledData() != null && !wellSurvey.getDrilledData().isEmpty()) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData().stream()
                    .filter(planned -> planned != null && drilledWellDepth.get(wellSurvey.getUid()) != null && planned.get("depth") != null && planned.get("coordinates") != null ?
                        Float.valueOf(planned.get("depth").toString()) >= drilledWellDepth.get(wellSurvey.getUid()) : false)
                    .map(drill -> (List<Double>) ((List) drill.get("coordinates")).stream().findFirst().get())
                    .collect(Collectors.toList()));
            } else if (wellSurvey.getPlannedData() != null && !wellSurvey.getPlannedData().isEmpty() && (wellSurvey.getDrilledData() == null || wellSurvey.getDrilledData().isEmpty())) {
                wellCoordinatesResponse.setPlannedData(wellSurvey.getPlannedData().stream()
                    .map(drill -> (List<Double>) ((List) drill.get("coordinates")).stream().findFirst().get())
                    .collect(Collectors.toList()));
            }
            else {
                wellCoordinatesResponse.setPlannedData(Collections.emptyList());
            }
            // set BHAs used count
            if(null != wellSurvey.getDistinctBHAsUsedCount()) {
                wellCoordinatesResponse.setDistinctBHAsUsedCount(wellSurvey.getDistinctBHAsUsedCount());
            } else{
                wellCoordinatesResponse.setDistinctBHAsUsedCount(0);
            }
            // Set Active rig name
            wellCoordinatesResponse.setActiveRigName(wellSurvey.getActiveRigName());
            latLngMap.putIfAbsent(wellSurvey.getUid(), wellCoordinatesResponse);
        }catch (Exception exp){
            log.error("Error occurred while processing wellborestick data for well: {}", wellSurvey.getUid(), exp);
        }

    }
    private void populateForWell(
        MongoWell well,
        Map<String, ROPs> ropByWellUidMap,
        Map<String, Cost> costByWellUidMap,
        Map<String, BHACount> bhaCountByUidMap,
        Map<String, WellData> wellMap,
        Map<String, WellCoordinatesResponseV2> latLngMap,
        Map<String, BHAHoleSize> bhaHoleSizeByUidMap
    ) {
        try {
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
                Location location = new Location(well.getLocation().getLng(), well.getLocation().getLat());
                wellCoordinatesResponse.setLocation(location);
            } else {
                wellCoordinatesResponse.getLocation().setLat(0.0);
                wellCoordinatesResponse.getLocation().setLng(0.0);
            }
            wellCoordinatesResponse.setDrilledData(new ArrayList<>());
            wellCoordinatesResponse.setPlannedData(new ArrayList<>());
            // set avgROP
            wellCoordinatesResponse.setAvgROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getAvgROP());
            wellCoordinatesResponse.setSlidingROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getSlidingROP());
            wellCoordinatesResponse.setRotatingROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getRotatingROP());
            wellCoordinatesResponse.setEffectiveROP(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getEffectiveROP());
            wellCoordinatesResponse.setCost(costByWellUidMap.getOrDefault(well.getUid(), new Cost()));
            wellCoordinatesResponse.setBhaCount(bhaCountByUidMap.getOrDefault(well.getUid(), new BHACount()));
            wellCoordinatesResponse.setBhaHoleSize(bhaHoleSizeByUidMap.getOrDefault(well.getUid(), new BHAHoleSize()));
            wellCoordinatesResponse.setTotalDays(wellMap.getOrDefault(well.getUid(), new WellData()).getTotalDays());
            wellCoordinatesResponse.setFootagePerDay(wellMap.getOrDefault(well.getUid(), new WellData()).getFootagePerDay());
            wellCoordinatesResponse.setSlidingPercentage(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getSlidingPercentage());
            wellCoordinatesResponse.setFootageDrilled(ropByWellUidMap.getOrDefault(well.getUid(), new ROPs()).getFootageDrilled());
            if(wellMap.get(well.getUid()) != null) {
                wellCoordinatesResponse.setHoleSectionRange(wellMap.get(well.getUid()).getHoleSectionRange());
            }
            wellCoordinatesResponse.setAvgDLSBySection(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgDLSBySection());
            wellCoordinatesResponse.setAvgMYBySection(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgMYBySection());
            wellCoordinatesResponse.setAvgDirectionAngle(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgDirectionAngle());
            wellCoordinatesResponse.setAvgDirection(wellMap.getOrDefault(well.getUid(), new WellData()).getAvgDirection());
            latLngMap.put(well.getUid(), wellCoordinatesResponse);
        }catch (Exception exp){
            log.error("Error occurred while processing well: {}", well.getUid(), exp);
        }

    }
    private Map<String, WellData> getWellDataMap() {
        final List<PerformanceWell> wellList = wellRepository.findAll();

        return wellList.stream()
            .collect(Collectors.toMap(
                PerformanceWell::getUid,
                WellsCoordinatesService::performanceWellToDto,
                (k1, k2) -> k1));
    }

    private Map<String, WellData> getWellDataMap(MongoWell well) {
        final PerformanceWell performanceWell = wellRepository.findFirstByUid(well.getUid());

        return performanceWell!=null?Map.of(performanceWell.getUid(), WellsCoordinatesService.performanceWellToDto(performanceWell)): Collections.emptyMap();
    }


    private Map<String, BHACount> getWellBHACountMap(List<PerformanceBHA> bhaList) {
        return bhaList.stream().filter(obj -> null != obj.getUid())
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhaSectionCountToDto,
                (k1, k2) -> k1));
    }

    private Map<String, BHAHoleSize> getWellBHAHoleSizeMap(List<PerformanceBHA> bhaList) {

        return bhaList.stream().filter(obj -> null != obj.getUid())
            .collect(Collectors.toMap(
                PerformanceBHA::getUid,
                WellsCoordinatesService::bhaSectionHoleSizeToDto,
                (k1, k2) -> k1));
    }



    private Map<String, BHACount> getWellBHACountMap(PerformanceBHA bha) {

        return bha != null ? Map.of(bha.getUid(), WellsCoordinatesService.bhaSectionCountToDto(bha)) : Collections.emptyMap();
    }

    private Map<String, BHAHoleSize> getWellBHAHoleSizeMap(PerformanceBHA bha) {
        return bha != null ? Map.of(bha.getUid(), WellsCoordinatesService.bhaSectionHoleSizeToDto(bha)) : Collections.emptyMap();
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

    private Map<String, Cost> getWellCostMap(MongoWell well) {
        final PerformanceCost cost = costRepository.findFirstByUid(well.getUid());
        return cost!=null?Map.of(cost.getUid(), WellsCoordinatesService.costToDto(cost)):Collections.emptyMap();
    }

    private Map<String, ROPs> getWellROPsMap() {
        final List<PerformanceROP> ropList = ropRepository.findAll();
        return ropList.stream()
            .collect(Collectors.toMap(
                PerformanceROP::getUid,
                WellsCoordinatesService::ropDomainToDto,
                (k1, k2) -> k1));
    }

    private Map<String, ROPs> getWellROPsMap(MongoWell well) {
        final PerformanceROP rop = ropRepository.findFirstByUid(well.getUid());
        return rop!=null?Map.of(rop.getUid(), WellsCoordinatesService.ropDomainToDto(rop)):Collections.emptyMap();
    }


    public String loadScript() {
        StringBuilder sb = new StringBuilder();
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
        return null != value ? BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue() : null;
    }
    private static Double dataConvertTwoDecimal(Double value){
        return  null != value && value >0 ?  Math.round(value * 100.0) / 100.0 : null;
    }

    private static Long dataRound(Double value){
        return null != value ?  Math.round(value) : null;
    }


    private static ROPs ropDomainToDto(final PerformanceROP ropDomain) {
        final ROPs ropDto = new ROPs();
        try {
            if (null != ropDomain.getAvgROP() && null != ropDomain.getAvgROP().getSection()) {
                final Section avgROPSection = new Section();
                avgROPSection.setAll(dataConvert(ropDomain.getAvgROP().getSection().getAll()));
                avgROPSection.setSurface(dataConvert(ropDomain.getAvgROP().getSection().getSurface()));
                avgROPSection.setIntermediate(dataConvert(ropDomain.getAvgROP().getSection().getIntermediate()));
                avgROPSection.setCurve(dataConvert(ropDomain.getAvgROP().getSection().getCurve()));
                avgROPSection.setLateral(dataConvert(ropDomain.getAvgROP().getSection().getLateral()));
                ROP ropAvg = new ROP();
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
                ROP slidingROP = new ROP();
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
                ROP rotatingROP = new ROP();
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
                ROP effectiveROP = new ROP();
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
                ROP sliderPercentage = new ROP();
                sliderPercentage.setSection(slidePercentageSection);
                ropDto.setSlidingPercentage(sliderPercentage);
            }
            if (null != ropDomain.getFootageDrilled()&& null != ropDomain.getFootageDrilled().getSection()) {
                final Section footageDrilledSection = new Section();
                footageDrilledSection.setAll(dataConvertTwoDecimal(ropDomain.getFootageDrilled().getSection().getAll()));
                footageDrilledSection.setSurface(dataConvertTwoDecimal(ropDomain.getFootageDrilled().getSection().getSurface()));
                footageDrilledSection.setIntermediate(dataConvertTwoDecimal(ropDomain.getFootageDrilled().getSection().getIntermediate()));
                footageDrilledSection.setCurve(dataConvertTwoDecimal(ropDomain.getFootageDrilled().getSection().getCurve()));
                footageDrilledSection.setLateral(dataConvertTwoDecimal(ropDomain.getFootageDrilled().getSection().getLateral()));
                ROP footageDrilled = new ROP();
                footageDrilled.setSection(footageDrilledSection);
                ropDto.setFootageDrilled(footageDrilled);
            }
        } catch (Exception e) {
            log.error("Error in ropDomainToDto for UID: {}",ropDomain.getUid(), e);

        }
        return ropDto;
    }

    private static Cost costToDto(final PerformanceCost domainCost) {
        final Cost cost = new Cost();
        try {
            cost.setAfe(dataConvert(domainCost.getCost().getAfe()));
            cost.setPerFt(dataConvert(domainCost.getCost().getPerFt()));
            cost.setPerLatFt(dataConvert(domainCost.getCost().getPerLatFt()));
            cost.setTotal(dataConvert(domainCost.getCost().getTotal()));
        } catch (Exception e) {
            log.error("Error in costToDto for UID: {}", domainCost.getUid());
        }
        return cost;
    }

    private static List<BHA> bhasToDto(final PerformanceBHA performanceBHA) {
        List<BHA> bhaList = new ArrayList<>();
        if (null != performanceBHA.getBha() && performanceBHA.getUid() != null && !performanceBHA.getBha().isEmpty())
            try {
                {
                    performanceBHA.getBha().forEach(bhaMongo -> {
                        BHA bha = new BHA();
                        bha.setId(bhaMongo.getId());
                        bha.setName(bhaMongo.getName());
                        bha.setMdStart(Math.round(bhaMongo.getMdStart()));
                        bha.setMdEnd(Math.round(bhaMongo.getMdEnd()));
                        bha.setMotorType(bhaMongo.getMotorType());
                        bha.setHoleSize(bhaMongo.getHoleSize());
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
                        bha.setFootageDrilled(new BHA.RopType(
                            new BHA.Section(
                                dataRound(bhaMongo.getFootageDrilled().getSection().getAll()),
                                dataRound(bhaMongo.getFootageDrilled().getSection().getSurface()),
                                dataRound(bhaMongo.getFootageDrilled().getSection().getIntermediate()),
                                dataRound(bhaMongo.getFootageDrilled().getSection().getCurve()),
                                dataRound(bhaMongo.getFootageDrilled().getSection().getLateral())
                            )));
                        bha.setAvgDLS(new BHA.RopType(
                            new BHA.Section(
                                dataConvertTwoDecimal(bhaMongo.getAvgDLSBySection().getSection().getAll()),
                                dataConvertTwoDecimal(bhaMongo.getAvgDLSBySection().getSection().getSurface()),
                                dataConvertTwoDecimal(bhaMongo.getAvgDLSBySection().getSection().getIntermediate()),
                                dataConvertTwoDecimal(bhaMongo.getAvgDLSBySection().getSection().getCurve()),
                                dataConvertTwoDecimal(bhaMongo.getAvgDLSBySection().getSection().getLateral())
                            )));
                        bha.setAvgMotorYield(new BHA.RopType(
                            new BHA.Section(
                                dataConvertTwoDecimal(bhaMongo.getAvgMYBySection().getSection().getAll()),
                                dataConvertTwoDecimal(bhaMongo.getAvgMYBySection().getSection().getSurface()),
                                dataConvertTwoDecimal(bhaMongo.getAvgMYBySection().getSection().getIntermediate()),
                                dataConvertTwoDecimal(bhaMongo.getAvgMYBySection().getSection().getCurve()),
                                dataConvertTwoDecimal(bhaMongo.getAvgMYBySection().getSection().getLateral())
                            )));
                        bha.setBuildWalkCompassAngle(new BHA.RopType(
                            new BHA.Section(
                                dataRound(bhaMongo.getAvgDirectionAngle().getSection().getAll()),
                                dataRound(bhaMongo.getAvgDirectionAngle().getSection().getSurface()),
                                dataRound(bhaMongo.getAvgDirectionAngle().getSection().getIntermediate()),
                                dataRound(bhaMongo.getAvgDirectionAngle().getSection().getCurve()),
                                dataRound(bhaMongo.getAvgDirectionAngle().getSection().getLateral())
                            )));
                        bha.setBuildWalkCompassDirection(new BHA.DirectionType(
                            new BHA.SectionDirection(
                                bhaMongo.getAvgDirection().getSection().getAll(),
                                bhaMongo.getAvgDirection().getSection().getSurface(),
                                bhaMongo.getAvgDirection().getSection().getIntermediate(),
                                bhaMongo.getAvgDirection().getSection().getCurve(),
                                bhaMongo.getAvgDirection().getSection().getLateral()
                            )));
                        bhaList.add(bha);

                    });
                }
            } catch (Exception e) {
                log.error("Error in bhasToDto for UID: {}",performanceBHA.getUid());
            }

        return bhaList;
    }


    private static BHACount bhaSectionCountToDto(final PerformanceBHA performanceBHA) {
        BHACount bhaCount = new BHACount();
        try {
            BHASectionCount section = new BHASectionCount();
            if (null != performanceBHA.getUid()) {
                section.setAll(performanceBHA.getBhaCount().getSection().getAll());
                section.setCurve(performanceBHA.getBhaCount().getSection().getCurve());
                section.setIntermediate(performanceBHA.getBhaCount().getSection().getIntermediate());
                section.setSurface(performanceBHA.getBhaCount().getSection().getSurface());
                section.setLateral(performanceBHA.getBhaCount().getSection().getLateral());
                bhaCount.setSection(section);
            }
        } catch (Exception e) {
            log.error("Error in bhaSectionCountToDto for UID: {}",performanceBHA.getUid());
        }
        return bhaCount;
    }

    private static BHAHoleSize bhaSectionHoleSizeToDto(final PerformanceBHA performanceBHA) {
        BHAHoleSize bhaHoleSize = new BHAHoleSize();
        try {
            BHASectionHoleSize section = new BHASectionHoleSize();
            if (null != performanceBHA.getUid()) {
                performanceBHA.getBha().forEach(bha -> {
                    if (bha.getSections().contains("surface")) {
                        section.getSurface().add(bha.getHoleSize());
                    }  if (bha.getSections().contains("intermediate")) {
                        section.getIntermediate().add(bha.getHoleSize());
                    }  if (bha.getSections().contains("curve")) {
                        section.getCurve().add(bha.getHoleSize());
                    }  if (bha.getSections().contains("lateral")) {
                        section.getLateral().add(bha.getHoleSize());
                    }
                    section.getAll().add(bha.getHoleSize());
                });
                bhaHoleSize.setSection(section);
            }
        } catch (Exception e) {
            log.error("Error in bhaSectionHoleSizeToDto for UID: {}", performanceBHA.getUid());
        }
        return bhaHoleSize;
    }

    private static WellData performanceWellToDto(final PerformanceWell performanceWell) {
        WellData wellData = new WellData();
        try {
            SectionData totalDays = new SectionData();
            totalDays.setSection(new WellDataSection(dataConvert(performanceWell.getTotalDays().getSection().getAll()),
                dataConvert(performanceWell.getTotalDays().getSection().getSurface()),
                dataConvert(performanceWell.getTotalDays().getSection().getIntermediate()),
                dataConvert(performanceWell.getTotalDays().getSection().getCurve()),
                dataConvert(performanceWell.getTotalDays().getSection().getLateral())));
            SectionData footagePerDay = new SectionData();
            footagePerDay.setSection(new WellDataSection(dataConvert(performanceWell.getFootagePerDay().getSection().getAll()),
                dataConvert(performanceWell.getFootagePerDay().getSection().getSurface()),
                dataConvert(performanceWell.getFootagePerDay().getSection().getIntermediate()),
                dataConvert(performanceWell.getFootagePerDay().getSection().getCurve()),
                dataConvert(performanceWell.getFootagePerDay().getSection().getLateral())));

            SectionData avgDLSBySection = new SectionData();
            avgDLSBySection.setSection(new WellDataSection(dataConvert(performanceWell.getAvgDLSBySection().getSection().getAll()),
                dataConvertTwoDecimal(performanceWell.getAvgDLSBySection().getSection().getSurface()),
                dataConvertTwoDecimal(performanceWell.getAvgDLSBySection().getSection().getIntermediate()),
                dataConvertTwoDecimal(performanceWell.getAvgDLSBySection().getSection().getCurve()),
                dataConvertTwoDecimal(performanceWell.getAvgDLSBySection().getSection().getLateral())));
            SectionData avgMYBySection = new SectionData();
            avgMYBySection.setSection(new WellDataSection(dataConvert(performanceWell.getAvgMYBySection().getSection().getAll()),
                dataConvertTwoDecimal(performanceWell.getAvgMYBySection().getSection().getSurface()),
                dataConvertTwoDecimal(performanceWell.getAvgMYBySection().getSection().getIntermediate()),
                dataConvertTwoDecimal(performanceWell.getAvgMYBySection().getSection().getCurve()),
                dataConvertTwoDecimal(performanceWell.getAvgMYBySection().getSection().getLateral())));
            SectionData avgDirectionAngle = new SectionData();
            avgDirectionAngle.setSection(new WellDataSection(dataConvert(performanceWell.getAvgDirectionAngle().getSection().getAll()),
                dataConvert(performanceWell.getAvgDirectionAngle().getSection().getSurface()),
                dataConvert(performanceWell.getAvgDirectionAngle().getSection().getIntermediate()),
                dataConvert(performanceWell.getAvgDirectionAngle().getSection().getCurve()),
                dataConvert(performanceWell.getAvgDirectionAngle().getSection().getLateral())));
            SectionDataDirection avgDirection = new SectionDataDirection();
            avgDirection.setSection(new SectionDirection(performanceWell.getAvgDirection().getSection().getAll(),
                performanceWell.getAvgDirection().getSection().getSurface(),
                performanceWell.getAvgDirection().getSection().getIntermediate(),
                performanceWell.getAvgDirection().getSection().getCurve(),
                performanceWell.getAvgDirection().getSection().getLateral()));

            Map<String, RangeData> holeSectionRange = new HashMap<>();
            performanceWell.getHoleSectionRange().entrySet().forEach(sec -> {
                holeSectionRange.put(getSectionKey(sec.getKey()), new RangeData(Math.round(sec.getValue().getStart()), Math.round(sec.getValue().getEnd()), Math.round(sec.getValue().getDiff()),Math.round(sec.getValue().getTvdStart()),Math.round(sec.getValue().getTvdEnd())));
            });
            wellData = new WellData(performanceWell.getUid(), totalDays, footagePerDay, avgDLSBySection, avgMYBySection, avgDirectionAngle, avgDirection, holeSectionRange);
        } catch (Exception e) {

            log.error("Error in performanceWellToDto for UID: {} {}",performanceWell.getUid(), e);
        }
        return wellData;
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

    public void sendWellUpdates(Set<String> wells) {

        List<MongoWell> mongoWells = mongoWellRepository.findAllByUidIn(List.copyOf(wells));
        mongoWells.forEach((well) -> {
            log.info("send update for: {}", well.getUid());
            try {
                if(null != well && null != well.getCustomer() && well.getCustomer().equalsIgnoreCase(COMPANY_NAME)){
                    restClientService.sendMessage("wellActivity", objectMapper.writeValueAsString(getWellCoordinates(well)));
                }  else {
                    log.error("Not a valid well {} for customer {}", well.getUid(), COMPANY_NAME);
                }
            } catch (JsonProcessingException e) {
                log.error("Error occur for sendWellUpdates ", e );
            }
            log.info("sent update for: {}", well.getUid());
        });
    }
}
