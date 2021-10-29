package com.moblize.ms.dailyops.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.dao.WellFormationDAO;
import com.moblize.ms.dailyops.domain.FormationMarker;
import com.moblize.ms.dailyops.domain.mongo.BCWDepthLog;
import com.moblize.ms.dailyops.domain.mongo.BCWSmoothLogData;
import com.moblize.ms.dailyops.domain.mongo.DepthLogResponse;
import com.moblize.ms.dailyops.dto.*;
import com.moblize.ms.dailyops.repository.mongo.client.BCWSmoothLogDataRepository;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import com.moblize.ms.dailyops.utils.NumberParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BCWDepthLogPlotService {

    static RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DrillingRoadMapFormationBuilder drillingRoadMapFormationBuilder;

    @Autowired
    private WellFormationDAO wellFormationDAO;

    @Autowired
    private BCWSmoothLogDataRepository bcwSmoothLogDataRepository;

    @Autowired
    private KpiDashboardClient kpiDashboardClient;

    @Value("${rest.nodedrilling.user}")
    public String NODE_USER_NAME;
    @Value("${rest.nodedrilling.pwd}")
    public String NODE_PASSWORD;
    @Value("${rest.nodedrilling.url}")
    public String NODE_DRILLING_SERVER_BASE_URL;

    String LOGDATA_PATH = "log";


    public BCWDepthPlotResponse getBCWDepthLog(BCWDepthPlotDTO bcwDepthPlotDTO) {
        BCWDepthPlotResponse bcwDepthPlotResponse = new BCWDepthPlotResponse();

        try {
            if(bcwDepthPlotDTO.getActionType() == null || bcwDepthPlotDTO.getActionType().isEmpty()){
                bcwDepthPlotDTO.setActionType("select");
            }

            if (bcwDepthPlotDTO.getActionType().equalsIgnoreCase("create")
                || bcwDepthPlotDTO.getActionType().equalsIgnoreCase("update")
                || bcwDepthPlotDTO.getActionType().equalsIgnoreCase("refresh")){
                bcwDepthPlotDTO.setStartIndex(0);
                bcwDepthPlotDTO.setEndIndex(50000);
            }

            //Get Drilling log Map data
            //Extract formationBcwData and sort by measure depth
            List<DrillingRoadMapWells> ls = this.getDrillingRoadmap(bcwDepthPlotDTO);

            //filter out the offset well by using start and end index

            List<DepthLogResponse> bcwDepthLog = new ArrayList<>();
            if (bcwDepthPlotDTO.getBcwId() != null && !bcwDepthPlotDTO.getBcwId().isEmpty()
                && bcwDepthPlotDTO.getActionType() != null && !bcwDepthPlotDTO.getActionType().isEmpty()) {

                log.debug("BCW Smooth Action Type: {} for Well UID: {} and BCW ID: {}", bcwDepthPlotDTO.getActionType(),
                    bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthPlotDTO.getBcwId());

                if (bcwDepthPlotDTO.getActionType().equalsIgnoreCase("select")) {
                    BCWSmoothLogData dbObj = findSmoothData(bcwDepthPlotDTO);
                    if(dbObj != null){
                        bcwDepthLog = dbObj.getDepthLogResponseList();
                    } else {
                        log.debug("No smoothed data found in DB, Get depth log for smoothing and saved it");
                        bcwDepthLog = processSmoothingAndSave(bcwDepthPlotDTO, ls);
                    }
                } else if (bcwDepthPlotDTO.getActionType().equalsIgnoreCase("create")
                    || bcwDepthPlotDTO.getActionType().equalsIgnoreCase("update")
                    || bcwDepthPlotDTO.getActionType().equalsIgnoreCase("refresh")) {
                    bcwDepthLog = processSmoothingAndSave(bcwDepthPlotDTO, ls);
                }
            }

            if(bcwDepthLog.isEmpty() && (bcwDepthPlotDTO.getBcwId() == null || bcwDepthPlotDTO.getBcwId().isEmpty())){
                log.debug("No Smoothing data found for well UID: {} Hence it's going to get sampled data",
                    bcwDepthPlotDTO.getPrimaryWellUid());

                bcwDepthLog = this.getOffSetLog1(ls, bcwDepthPlotDTO.getEndIndex(), bcwDepthPlotDTO.getStartIndex(), false);
            }

            //  saveBCWDepthLog(bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthLog);
            bcwDepthPlotResponse.setStatus("success");
            bcwDepthPlotResponse.setMessage("success");
            bcwDepthPlotResponse.setData(bcwDepthLog);
        } catch (Exception e) {
            log.error("Error occur in getBCWDepthLog for well uid {}", bcwDepthPlotDTO.getPrimaryWellUid(),e);
            bcwDepthPlotResponse.setStatus("invalid");
            bcwDepthPlotResponse.setMessage(e.getMessage());
            bcwDepthPlotResponse.setData(null);
        }

        return bcwDepthPlotResponse;
    }

    private List<DepthLogResponse> processSmoothingAndSave(BCWDepthPlotDTO bcwDepthPlotDTO, List<DrillingRoadMapWells> ls) {
        List<DepthLogResponse> bcwDepthLog;
        bcwDepthLog = this.getOffSetLog1(ls, bcwDepthPlotDTO.getEndIndex(), bcwDepthPlotDTO.getStartIndex(), true);
        bcwDepthLog = smoothData(bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthLog);
        saveUpdateSmoothData(bcwDepthPlotDTO, bcwDepthLog);
        return bcwDepthLog;
    }

    private void saveUpdateSmoothData(BCWDepthPlotDTO bcwDepthPlotDTO, List<DepthLogResponse> bcwDepthLog) {
        BCWSmoothLogData dbObj = findSmoothData(bcwDepthPlotDTO);
        if (dbObj == null) {
            log.debug("SAVE: Smooth data for well id {}, BCW ID: {}",bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthPlotDTO.getBcwId());

            dbObj = new BCWSmoothLogData();
            dbObj.setBcwId(bcwDepthPlotDTO.getBcwId());
            dbObj.setUid(bcwDepthPlotDTO.getPrimaryWellUid());
            dbObj.setDepthLogResponseList(bcwDepthLog);
        } else {
            log.debug("UPDATE: Smooth data for well id {}, BCW ID: {}",bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthPlotDTO.getBcwId());
            dbObj.setDepthLogResponseList(bcwDepthLog);
        }
        bcwSmoothLogDataRepository.save(dbObj);
    }

    private BCWSmoothLogData findSmoothData(BCWDepthPlotDTO bcwDepthPlotDTO) {
        BCWSmoothLogData bcwSmoothLogData = bcwSmoothLogDataRepository.findBCWSmoothLogDataByBcwId(bcwDepthPlotDTO.getBcwId());
        if(bcwSmoothLogData != null && !bcwSmoothLogData.getDepthLogResponseList().isEmpty()) {
            bcwSmoothLogData.setDepthLogResponseList(bcwSmoothLogData.getDepthLogResponseList().stream()
                .filter(log -> log.getHoleDepth() >= bcwDepthPlotDTO.getStartIndex()
                    && log.holeDepth <= bcwDepthPlotDTO.getEndIndex()).collect(Collectors.toList()));
        }
        return bcwSmoothLogData;

    }


    public List<DepthLogResponse> smoothData(String wellUid, List<DepthLogResponse> bcwDepthLog) {

        List<DepthLogResponse> smoothedBCWData = new ArrayList<>();

        final List<Double> highestRopList = new ArrayList<>();
        final List<Double> rpmaList = new ArrayList<>();
        final List<Double> diffpressureList = new ArrayList<>();
        final List<Double> mudFlowList = new ArrayList<>();
        final List<Double> pumpPressureList = new ArrayList<>();
        final List<Double> surfaceTorqueMaxList = new ArrayList<>();
        final List<Double> weightonBitMaxList = new ArrayList<>();

        if (null != bcwDepthLog && !bcwDepthLog.isEmpty()) {

            Map<HoleSection.HoleSectionType,HoleSection> holeSectionMap = new HashMap<>();
            List<HoleSection> holeSectionList =  kpiDashboardClient.getHoleSections(wellUid);
            if(holeSectionList != null && !holeSectionList.isEmpty()) {
                Collections.sort(holeSectionList, (o1, o2) -> {
                    return o1.getFromDepth() > o2.getFromDepth() ? 1 : -1 ;
                });
                holeSectionMap = holeSectionList.stream().collect(Collectors.toMap(x -> x.getSection(), Function.identity()));
            }
            Double firstDepth = bcwDepthLog.get(0).getHoleDepth();
            Double lastDepth = bcwDepthLog.get(bcwDepthLog.size() - 1).getHoleDepth();
            log.info("FirstDepth: {} ,LastDepth: {}", firstDepth, lastDepth);

            DepthClass depthClass = new DepthClass();
            Map<HoleSection.HoleSectionType, HoleSection> finalHoleSectionMap = holeSectionMap;
            bcwDepthLog.stream()
                .filter(log -> {
                    HoleSection holeSection = finalHoleSectionMap.get(HoleSection.HoleSectionType.CURVE);
                    if (holeSection == null && !log.getRigState().equalsIgnoreCase("SLIDE DRILLING")) {
                        return true;
                    } else if (holeSection != null && log.getHoleDepth() > holeSection.getFromDepth() && log.getHoleDepth() <= holeSection.getToDepth()) {
                        return true;
                    } else if (holeSection != null
                        && (log.getHoleDepth() <= holeSection.getFromDepth() || log.getHoleDepth() > holeSection.getToDepth())
                        && !log.getRigState().equalsIgnoreCase("SLIDE DRILLING")) {
                        return true;
                    }
                    return false;
                    })
                .forEach(depthLogObj -> {
                    if (depthClass.holeDepth < 0d) {
                        depthClass.holeDepth = depthLogObj.getHoleDepth();
                        depthClass.holeDepthFirstRound = firstDepth;
                        depthClass.holeDepthNextRound = depthClass.holeDepthFirstRound + 100d;
                        log.debug("DepthLogObj :"+depthLogObj.getHoleDepth()+" HoleDepth " + depthClass.holeDepth + " holeDepthFirstRound: " + depthClass.holeDepthFirstRound + " holeDepthNextRound: " + depthClass.holeDepthNextRound);
                    }
                    if ( //depthLogObj.getHoleDepth() % 100 == 0 ||
                        (depthLogObj.getHoleDepth() >= depthClass.holeDepthFirstRound && depthLogObj.getHoleDepth() < depthClass.holeDepthNextRound)
                       || (depthLogObj.getHoleDepth() >= depthClass.holeDepthFirstRound && depthLogObj.getHoleDepth() >= depthClass.holeDepthNextRound)
                        || depthLogObj.getHoleDepth() == lastDepth) {

                        BCWDepthLog bcwDepthLog1Object = smoothDataForPerHundredFeet(depthClass.holeDepth, highestRopList, rpmaList, diffpressureList, mudFlowList, pumpPressureList, surfaceTorqueMaxList, weightonBitMaxList);
                        depthLogObj.setRopAvg(bcwDepthLog1Object.getHighestRopAvg());
                        depthLogObj.setRpma(bcwDepthLog1Object.getRpmaAvg());
                        depthLogObj.setDiffPressure(bcwDepthLog1Object.getDiffPressureAvg());
                        depthLogObj.setMudFlowInAvg(bcwDepthLog1Object.getMudFlowAvg());
                        depthLogObj.setPumpPressure(bcwDepthLog1Object.getPumpPressureAvg());
                        depthLogObj.setSurfaceTorqueMax(bcwDepthLog1Object.getSurfaceTorqueAvg());
                        depthLogObj.setWeightonBitMax(bcwDepthLog1Object.getWeightOnBitAvg());
                        smoothedBCWData.add(depthLogObj);

                       HoleSection currHoleSection = holeSectionList.get(depthClass.currentHoleSectionIndex);

                       if (depthLogObj.getHoleDepth() > depthClass.holeDepthNextRound) {
                            depthClass.holeDepth = Double.valueOf(depthClass.holeDepthFirstRound);
                            //depthClass.holeDepthFirstRound = ((Math.round(depthLogObj.getHoleDepth())) / 100) * 100;
                            depthClass.holeDepthFirstRound = depthLogObj.getHoleDepth();
                            depthClass.holeDepthNextRound = depthClass.holeDepthFirstRound + 100;
                        } else {
                            depthClass.holeDepth = Double.valueOf(depthClass.holeDepthFirstRound);
                            depthClass.holeDepthFirstRound = depthClass.holeDepthNextRound;
                            depthClass.holeDepthNextRound = depthClass.holeDepthNextRound + 100;
                        }

                       if( depthClass.holeDepthNextRound > currHoleSection.getToDepth()  && !depthClass.lateralCrossed){
                           depthClass.holeDepthNextRound =  currHoleSection.getToDepth().doubleValue();
                           if(currHoleSection.getSection().equals(HoleSection.HoleSectionType.LATERAL)){
                               depthClass.lateralCrossed = true;
                           } else {
                               depthClass.currentHoleSectionIndex++;
                           }
                           log.debug("HoleSection range at depth {} and next holesection is {} ", depthClass.holeDepthNextRound,
                               holeSectionList.get(depthClass.currentHoleSectionIndex).getSection().name() );
                       }
                        log.debug("DepthLogObj :"+depthLogObj.getHoleDepth()+" HoleDepth " + depthClass.holeDepth + " holeDepthFirstRound: " +
                            depthClass.holeDepthFirstRound + " holeDepthNextRound: " + depthClass.holeDepthNextRound);
                       /* depthClass.holeDepth = Double.valueOf(depthClass.holeDepthFirstRound);
                        depthClass.holeDepthFirstRound = depthClass.holeDepthNextRound;
                        depthClass.holeDepthNextRound = depthClass.holeDepthNextRound+ 100;*/



                        highestRopList.clear();
                        rpmaList.clear();
                        diffpressureList.clear();
                        mudFlowList.clear();
                        pumpPressureList.clear();
                        surfaceTorqueMaxList.clear();
                        weightonBitMaxList.clear();
                    }
                    if (depthLogObj.getRopAvg() != null) {
                        highestRopList.add(depthLogObj.getRopAvg());
                    } else {
                        log.debug("Heighest ROP null : log index {}", depthLogObj.getIndex());
                    }
                    if (depthLogObj.getRpma() != null) {
                        rpmaList.add(depthLogObj.getRpma());
                    } else {
                        log.debug("RPMA ROP null : log index {}", depthLogObj.getIndex());
                    }
                    if (depthLogObj.getDiffPressure() != null) {
                        diffpressureList.add(depthLogObj.getDiffPressure());
                    } else {
                        log.debug("Diff Pressure ROP null : log index {}", depthLogObj.getIndex());
                    }
                    if (depthLogObj.getMudFlowInAvg() != null) {
                        mudFlowList.add(depthLogObj.getMudFlowInAvg());
                    } else {
                        log.debug("Mud Flow ROP null : log index {}", depthLogObj.getIndex());
                    }
                    if (depthLogObj.getPumpPressure() != null) {
                        pumpPressureList.add(depthLogObj.getPumpPressure());
                    } else {
                        log.debug("Pump pressure ROP null : log index {}", depthLogObj.getIndex());
                    }
                    if (depthLogObj.getSurfaceTorqueMax() != null) {
                        surfaceTorqueMaxList.add(depthLogObj.getSurfaceTorqueMax());
                    } else {
                        log.debug("Surface Torque null : log index {}", depthLogObj.getIndex());
                    }
                    if (depthLogObj.getWeightonBitMax() != null) {
                        weightonBitMaxList.add(depthLogObj.getWeightonBitMax());
                    } else {
                        log.debug("Weight On Bit Max null : log index {}", depthLogObj.getIndex());
                    }
                });

        }

        return smoothedBCWData;

    }

    public class DepthClass {
        public Double holeDepth = -1d;
        public Double holeDepthFirstRound = -1d;
        public Double holeDepthNextRound = -1d;
        public int currentHoleSectionIndex = 0;
        public boolean lateralCrossed = false;
    }

    private BCWDepthLog smoothDataForPerHundredFeet(Double holeDepth, List<Double> highestRopList, List<Double> rpmaList, List<Double> diffpressureList, List<Double> mudFlowList, List<Double> pumpPressureList, List<Double> surfaceTorqueMaxList, List<Double> weightonBitMaxList) {
        // Insert new metadata
        BCWDepthLog bcwDepthLogObject = new BCWDepthLog();
        bcwDepthLogObject.setUid("");
        bcwDepthLogObject.setHoleDepth(holeDepth);

        if (!highestRopList.isEmpty()) {
            final Double sum = highestRopList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = highestRopList.size();
            bcwDepthLogObject.setHighestRopCount(count);
            bcwDepthLogObject.setHighestRopSum(sum.floatValue());
            bcwDepthLogObject.setHighestRopAvg(sum / count);
        }

        if (!rpmaList.isEmpty()) {
            final Double sum = rpmaList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = rpmaList.size();
            bcwDepthLogObject.setRpmaCount(count);
            bcwDepthLogObject.setRpmaSum(sum.floatValue());
            bcwDepthLogObject.setRpmaAvg(sum / count);
        }

        if (!diffpressureList.isEmpty()) {
            final Double sum = diffpressureList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = diffpressureList.size();
            bcwDepthLogObject.setDiffPressureCount(count);
            bcwDepthLogObject.setDiffPressureSum(sum.floatValue());
            bcwDepthLogObject.setDiffPressureAvg(sum / count);
        }

        if (!mudFlowList.isEmpty()) {
            final Double sum = mudFlowList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = mudFlowList.size();
            bcwDepthLogObject.setMudFlowCount(count);
            bcwDepthLogObject.setMudFlowSum(sum.floatValue());
            bcwDepthLogObject.setMudFlowAvg(sum / count);
        }

        if (!pumpPressureList.isEmpty()) {
            final Double sum = pumpPressureList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = pumpPressureList.size();
            bcwDepthLogObject.setPumpPressureCount(count);
            bcwDepthLogObject.setPumpPressureSum(sum.floatValue());
            bcwDepthLogObject.setPumpPressureAvg(sum / count);

        }

        if (!surfaceTorqueMaxList.isEmpty()) {
            final Double sum = surfaceTorqueMaxList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = surfaceTorqueMaxList.size();
            bcwDepthLogObject.setSurfaceTorqueCount(count);
            bcwDepthLogObject.setSurfaceTorqueSum(sum.floatValue());
            bcwDepthLogObject.setSurfaceTorqueAvg(sum / count);
        }

        if (!weightonBitMaxList.isEmpty()) {
            final Double sum = weightonBitMaxList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = weightonBitMaxList.size();
            bcwDepthLogObject.setWeightOnBitCount(count);
            bcwDepthLogObject.setWeightOnBitSum(sum.floatValue());
            bcwDepthLogObject.setWeightOnBitAvg(sum / count);
        }

        return bcwDepthLogObject;
    }


   /* public void saveBCWDepthLog(final String wellUID, final List<Map<String, Object>> bcwDepthLogData) {
        BCWDepthLog bcwDepthLog = new BCWDepthLog();
        bcwDepthLog.setUid(wellUID);
        bcwDepthLog.setBcwDepthLog(bcwDepthLogData);

        bcwDepthLogService.saveUpdateBCWDepthLog(bcwDepthLog);

    }*/

    private List<DrillingRoadMapWells> getDrillingRoadmap(BCWDepthPlotDTO bcwDepthPlotDTO) {
        Map<String, List<FormationMarker>> formationMarkerMap = drillingRoadMapFormationBuilder.getFormationMap(bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthPlotDTO.getOffsetWellUids(), "Wellbore1");

        List<String> primaryWellFormation = formationMarkerMap.get(bcwDepthPlotDTO.getPrimaryWellUid()).stream().map(formation -> formation.getName()).collect(Collectors.toList());

        Set<String> formationWellList = formationMarkerMap.keySet();
        formationWellList.remove(bcwDepthPlotDTO.getPrimaryWellUid());

        List<DrillingRoadMapWells> bcwFormationData = primaryWellFormation.isEmpty() || formationWellList.isEmpty() ? Collections.emptyList() : wellFormationDAO.getBCWDataFormation(new ArrayList<>(formationWellList), primaryWellFormation);

        List<DrillingRoadMapWells> filterList = new ArrayList<>();
        if (bcwFormationData != null && !bcwFormationData.isEmpty()) {
            List<DrillingRoadMapWells> ls = bcwFormationData;
            shortFilterList(ls);

            Set<DrillingRoadMapWells> filterListSet = new LinkedHashSet<>();
            IntStream.range(0, ls.size() - 1).forEachOrdered(i -> {
                DrillingRoadMapWells currentDrillRoadMapWell = ls.get(i);
                DrillingRoadMapWells nextDrillRoadMapWell = ls.get(i + 1);

                if ((i == 0 && isMDGreaterThanRange(bcwDepthPlotDTO, currentDrillRoadMapWell))
                    || isMDInRange(bcwDepthPlotDTO, currentDrillRoadMapWell)
                    || (NumberParserUtils.intParse(currentDrillRoadMapWell.getMD()) <= bcwDepthPlotDTO.getStartIndex() &&
                    NumberParserUtils.intParse(nextDrillRoadMapWell.getMD()) >= bcwDepthPlotDTO.getStartIndex())) {
                    filterListSet.add(currentDrillRoadMapWell);
                }

                if (isMDInRange(bcwDepthPlotDTO, nextDrillRoadMapWell)
                    || (i + 1 == ls.size() - 1
                    && NumberParserUtils.intParse(nextDrillRoadMapWell.getMD()) <= bcwDepthPlotDTO.getEndIndex())) {
                    filterListSet.add(nextDrillRoadMapWell);
                }

            });

            filterList = new ArrayList<>(filterListSet);
            shortFilterList(filterList);


        }

        return filterList;
    }

    private boolean isMDGreaterThanRange(BCWDepthPlotDTO bcwDepthPlotDTO, DrillingRoadMapWells currentDrillRoadMapWell) {
        return NumberParserUtils.intParse(currentDrillRoadMapWell.getMD()) >= bcwDepthPlotDTO.getStartIndex()
            && NumberParserUtils.intParse(currentDrillRoadMapWell.getMD()) >= bcwDepthPlotDTO.getEndIndex();
    }

    private boolean isMDInRange(BCWDepthPlotDTO bcwDepthPlotDTO, DrillingRoadMapWells currentDrillRoadMapWell) {
        return NumberParserUtils.intParse(currentDrillRoadMapWell.getMD()) >= bcwDepthPlotDTO.getStartIndex()
            && NumberParserUtils.intParse(currentDrillRoadMapWell.getMD()) <= bcwDepthPlotDTO.getEndIndex();
    }

    private void shortFilterList(List<DrillingRoadMapWells> filterList) {
        Collections.sort(filterList, new Comparator<DrillingRoadMapWells>() {
            @Override
            public int compare(DrillingRoadMapWells obj1, DrillingRoadMapWells obj2) {
                double d1 = Double.parseDouble(obj1.getMD());
                double d2 = Double.parseDouble(obj2.getMD());
                int checkValue = d1 > d2 ? 1 : -1;
                return d1 == d2 ? 0 : checkValue;
            }
        });
    }

    private List<Map<String, Object>> getOffSetLog(List<DrillingRoadMapWells> offSetList, int lastOffSetEndIndex, int lastOffSetStartIndex) {

        List<LogDataRequestDTO> requestDTOList = setStartAndEndIndex(offSetList, lastOffSetEndIndex, lastOffSetStartIndex);

        List<List<Map<String, Object>>> logList = requestDTOList.parallelStream()
            .map(ls -> {
                    List<Map<String, Object>> logData = getLogData("depth", ls.getWellUID(), String.valueOf(ls.getStartIndex()), String.valueOf(ls.getEndIndex()), 1000, false, true, null);
                    try {
                        logData = logData.parallelStream().filter(
                            k -> NumberParserUtils.floatParse(k.get("HoleDepth").toString()) >= ls.getStartIndex()
                                && NumberParserUtils.floatParse(k.get("HoleDepth").toString()) <= ls.getEndIndex())
                            .collect(Collectors.toList());
                        logData.parallelStream().forEach(map -> map.put("offSetWellUid", ls.getWellUID()));
                        return logData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        List<Map<String, Object>> logData1 = new ArrayList<>();
                        return logData1;
                    }
                }
            ).collect(Collectors.toList());

        List<Map<String, Object>> consolidateList = logList.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        logList.size();

        return consolidateList;
    }

    private List<DepthLogResponse> getOffSetLog1(List<DrillingRoadMapWells> offSetList, int lastOffSetEndIndex, int lastOffSetStartIndex, boolean disableReduce) {

        List<LogDataRequestDTO> requestDTOList = setStartAndEndIndex(offSetList, lastOffSetEndIndex, lastOffSetStartIndex);

        List<List<DepthLogResponse>> logList = requestDTOList.parallelStream()
            .map(ls -> {
                    List<DepthLogResponse> logData = getDepthLogData("depth", ls.getWellUID(), String.valueOf(ls.getStartIndex()), String.valueOf(ls.getEndIndex()), disableReduce ? 30000 : 1000, disableReduce, true, null);
                    try {
                        logData = logData.parallelStream().filter(
                            k -> k.getHoleDepth() >= ls.getStartIndex()
                                && k.getHoleDepth() <= ls.getEndIndex())
                            .collect(Collectors.toList());
                        //logData.parallelStream().forEach(map -> map.put("offSetWellUid", ls.getWellUID()));
                        return logData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        List<DepthLogResponse> logData1 = new ArrayList<>();
                        return logData1;
                    }
                }
            ).collect(Collectors.toList());

        List<DepthLogResponse> consolidateList = logList.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        logList.size();

        return consolidateList;
    }

    private List<LogDataRequestDTO> setStartAndEndIndex(List<DrillingRoadMapWells> offSetList, int lastOffSetEndIndex, int lastOffSetStartIndex) {
        List<LogDataRequestDTO> requestDTOList = new LinkedList<>();
        log.debug(" StartIndex: " + lastOffSetStartIndex + " EndIndex: " + lastOffSetEndIndex);
        for (int listIndex = 0; listIndex < offSetList.size(); listIndex++) {
            DrillingRoadMapWells startIndexObj = offSetList.get(listIndex);
            DrillingRoadMapWells endIndexObj = listIndex + 1 == offSetList.size() ? startIndexObj : offSetList.get(listIndex + 1);
            log.debug("startIndexObj.getWellUid() " + startIndexObj.getWellUid() + " MD: " + startIndexObj.getMD() + "  endIndexObj.getWellUid(): " + endIndexObj.getWellUid() + "  MD: " + endIndexObj.getMD());
            LogDataRequestDTO logDataRequestDTO = new LogDataRequestDTO();
            logDataRequestDTO.setWellUID(startIndexObj.getWellUid());
            if (listIndex == 0) {
                if (listIndex == offSetList.size() - 1) {
                    logDataRequestDTO.setStartIndex(lastOffSetStartIndex);
                    logDataRequestDTO.setEndIndex(lastOffSetEndIndex);
                } else {
                    logDataRequestDTO.setStartIndex(NumberParserUtils.intParse(startIndexObj.getMD()) < lastOffSetStartIndex ? lastOffSetStartIndex : NumberParserUtils.intParse(startIndexObj.getMD()));
                    logDataRequestDTO.setEndIndex(NumberParserUtils.intParse(endIndexObj.getMD()));
                }
            } else if (listIndex == offSetList.size() - 1) {
                logDataRequestDTO.setStartIndex(NumberParserUtils.intParse(startIndexObj.getMD()));
                logDataRequestDTO.setEndIndex(lastOffSetEndIndex);
            } else {
                logDataRequestDTO.setStartIndex(NumberParserUtils.intParse(startIndexObj.getMD()));
                logDataRequestDTO.setEndIndex(NumberParserUtils.intParse(endIndexObj.getMD()));
            }
            requestDTOList.add(logDataRequestDTO);
        }
        return requestDTOList;
    }

    private List<Map<String, Object>> getLogData(String type, String wellUid, String startIndex, String endIndex,
                                                 Integer limit, Boolean disableReduce, Boolean useUserUOMSettings, List<String> channels) {
        long startTime = System.currentTimeMillis();

        if (type.equals("time")) {

            if (disableReduce && limit == -1) {
                throw new IllegalArgumentException("You can't disable reduction but get all the data. It will be harmful to the system.");
            }

            if (Long.parseLong(startIndex) < 946684800000L) { // if before 1/1/2000, let's not
                throw new IllegalArgumentException("You are requesting for data before 1/1/2000. Is this intentional? If so, please contact admin. The system currently does not allow that");
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("wellUid", wellUid);
        params.put("startIndex", startIndex);
        params.put("endIndex", endIndex);

        params.put("limit", limit.toString());
        params.put("disableReduce", disableReduce.toString());
        if (channels != null && !channels.isEmpty()) {
            try {
                params.put("channels", objectMapper.writeValueAsString(channels));
            } catch (Exception e) {
                log.error("Bad channels", e);
            }
        }
        List<Map<String, Object>> logObj = null;
        ResponseEntity<JsonNode> response = getLatestCustomChannel(params);
        if (response.getStatusCode().is2xxSuccessful()) {
            logObj = objectMapper.convertValue(response.getBody().get("data"), new ArrayList<Map<String, Object>>().getClass());
        } else {
            return null;
        }

        long endTime = System.currentTimeMillis();
        log.info(String.format("Took %f (s) to get log data from node-drilling-log service, found %d records, type: " + type + " for %s , start: %s, end: %s",
            Long.valueOf(endTime - startTime).floatValue() / 1000.0f, logObj.size(), wellUid, startIndex, endIndex));

        return logObj;
    }

    private List<DepthLogResponse> getDepthLogData(String type, String wellUid, String startIndex, String endIndex,
                                                   Integer limit, Boolean disableReduce, Boolean useUserUOMSettings, List<String> channels) {
        long startTime = System.currentTimeMillis();

        if (type.equals("time")) {

            if (disableReduce && limit == -1) {
                throw new IllegalArgumentException("You can't disable reduction but get all the data. It will be harmful to the system.");
            }

            if (Long.parseLong(startIndex) < 946684800000L) { // if before 1/1/2000, let's not
                throw new IllegalArgumentException("You are requesting for data before 1/1/2000. Is this intentional? If so, please contact admin. The system currently does not allow that");
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("wellUid", wellUid);
        params.put("startIndex", startIndex);
        params.put("endIndex", endIndex);

        params.put("limit", limit.toString());
        params.put("disableReduce", disableReduce.toString());
        if (channels != null && !channels.isEmpty()) {
            try {
                params.put("channels", objectMapper.writeValueAsString(channels));
            } catch (Exception e) {
                log.error("Bad channels", e);
            }
        }
        List<DepthLogResponse> logObj = null;
        ResponseEntity<LogResponse> response = getLatestCustomChannel1(params);

        if (response.getStatusCode().is2xxSuccessful()) {
            logObj = response.getBody().getData();
        } else {
            return null;
        }

        long endTime = System.currentTimeMillis();
        log.info(String.format("Took %f (s) to get log data from node-drilling-log service, found %d records, type: " + type + " for %s , start: %s, end: %s",
            Long.valueOf(endTime - startTime).floatValue() / 1000.0f, logObj.size(), wellUid, startIndex, endIndex));

        return logObj;
    }


    public ResponseEntity<JsonNode> getLatestCustomChannel(Map<String, String> params) {
        String url = NODE_DRILLING_SERVER_BASE_URL + LOGDATA_PATH;
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.setAll(params);
        URI uri = UriComponentsBuilder.newInstance()
            .fromUriString(url)
            .queryParams(map).build().toUri();
        final ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders(NODE_USER_NAME, NODE_PASSWORD)),
            new ParameterizedTypeReference<JsonNode>() {
            });
        return responseEntity;
    }

    public ResponseEntity<LogResponse> getLatestCustomChannel1(Map<String, String> params) {
        String url = NODE_DRILLING_SERVER_BASE_URL + LOGDATA_PATH;
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.setAll(params);
        URI uri = UriComponentsBuilder.newInstance()
            .fromUriString(url)
            .queryParams(map).build().toUri();
        final ResponseEntity<LogResponse> responseEntity = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            new HttpEntity<>(createHeaders(NODE_USER_NAME, NODE_PASSWORD)),
            new ParameterizedTypeReference<LogResponse>() {
            });
        return responseEntity;
    }

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
            set("Content-Type", "application/json");

        }};
    }
}
