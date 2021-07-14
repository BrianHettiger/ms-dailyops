package com.moblize.ms.dailyops.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.client.WitsmlLogsClient;
import com.moblize.ms.dailyops.dao.WellFormationDAO;
import com.moblize.ms.dailyops.domain.FormationMarker;
import com.moblize.ms.dailyops.domain.mongo.BCWDepthLog;
import com.moblize.ms.dailyops.dto.*;
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

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
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
    private WitsmlLogsClient witsmlLogsClient;

    @Autowired
    private BCWDepthLogService bcwDepthLogService;

    @Value("${ALTERNATE_LOCALHOST:172.31.3.62}")
    public String ALTERNATE_LOCALHOST;
    @Value("${api.central.nodedrilling.user}")
    public String NODE_USER_NAME;
    @Value("${api.central.nodedrilling.pwd}")
    public String NODE_PASSWORD;
    @Value("${api.defaulttenant.nodedrilling.base.url}")
    public String NODE_DRILLING_SERVER_BASE_URL;

    String LOGDATA_PATH = "log";

    @PostConstruct
    public void initRestClientService() {
        if (ALTERNATE_LOCALHOST != null) {
            NODE_DRILLING_SERVER_BASE_URL = NODE_DRILLING_SERVER_BASE_URL.replace("localhost", ALTERNATE_LOCALHOST);
        }
    }

    public List<DepthLogReponse> getBCWDepthLog(BCWDepthPlotDTO bcwDepthPlotDTO) {

        //Get Drilling log Map data
        //Extract formationBcwData and sort by measure depth
        List<DrillingRoadMapWells> ls = this.getDrillingRoadmap(bcwDepthPlotDTO);

        //filter out the offset well by using start and end index

        List<DepthLogReponse> bcwDepthLog = this.getOffSetLog1(ls, bcwDepthPlotDTO.getEndIndex(), bcwDepthPlotDTO.getStartIndex());



        //  saveBCWDepthLog(bcwDepthPlotDTO.getPrimaryWellUid(), bcwDepthLog);

        return  smoothData(bcwDepthLog);
    }


    public List<DepthLogReponse> smoothData(List<DepthLogReponse> bcwDepthLog) {

        List<DepthLogReponse> smoothedBCWData = new ArrayList<>();

        final List<Double> highestRopList = new ArrayList<>();
        final List<Double> rpmaList = new ArrayList<>();
        final List<Double> diffpressureList = new ArrayList<>();
        final List<Double> mudFlowList = new ArrayList<>();
        final List<Double> pumpPressureList = new ArrayList<>();
        final List<Double> surfaceTorqueMaxList = new ArrayList<>();
        final List<Double> weightonBitMaxList = new ArrayList<>();

        if (null != bcwDepthLog && !bcwDepthLog.isEmpty()) {
            Double firstDepth = bcwDepthLog.get(0).getHoleDepth();
            Double lastDepth = bcwDepthLog.get(bcwDepthLog.size() - 1).getHoleDepth();
            double firstDepthRounded = ((Math.round(firstDepth) + 99) / 100) * 100;
            System.out.println(firstDepth + " " + firstDepthRounded + " " + lastDepth);
            long rangeStart = Math.round(firstDepthRounded) / 100;
            long rangeEnd = Math.round(lastDepth) / 100;

            DepthClass depthClass = new DepthClass();
            bcwDepthLog
                .forEach(i -> {
                    if (depthClass.holeDepth < 0d) {
                        depthClass.holeDepth = i.getHoleDepth();
                        depthClass.holeDepthFirstRound = ((Math.round(firstDepth) + 99) / 100) * 100;
                        depthClass.holeDepthNextRound = depthClass.holeDepthFirstRound + 100;
                    }
                    if (i.getHoleDepth() % 100 == 0 ||
                        (i.getHoleDepth() > depthClass.holeDepthFirstRound && i.getHoleDepth() <= depthClass.holeDepthNextRound)
                        || i.getHoleDepth() == lastDepth) {

                        log.debug("HoleDepth " + depthClass.holeDepth + " DepthClass.holeDepthFirstRound: " + depthClass.holeDepthFirstRound + " DepthClass.holeDepthNextRound: " + depthClass.holeDepthNextRound);

                        BCWDepthLog bcwDepthLog1Object = smoothDataForPerHundredFeet(depthClass.holeDepth, highestRopList, rpmaList, diffpressureList, mudFlowList, pumpPressureList, surfaceTorqueMaxList, weightonBitMaxList);
                    //    smoothedBCWData.add(bcwDepthLogService.saveUpdateBCWDepthLog(bcwDepthLog1Object));

                        depthClass.holeDepth = Double.valueOf(depthClass.holeDepthFirstRound);
                        depthClass.holeDepthFirstRound = depthClass.holeDepthNextRound;
                        depthClass.holeDepthNextRound = depthClass.holeDepthNextRound+ 100;

                        highestRopList.clear();
                        rpmaList.clear();
                        diffpressureList.clear();
                        mudFlowList.clear();
                        pumpPressureList.clear();
                        surfaceTorqueMaxList.clear();
                        weightonBitMaxList.clear();
                    }
                    if (i.getROPAvg() != null) {
                        highestRopList.add(i.getROPAvg());
                    } else {
                        log.debug("Heighest ROP null : log index {}", i.getIndex());
                    }
                    if (i.getRpma() != null) {
                        rpmaList.add(i.getRpma());
                    } else {
                        log.debug("RPMA ROP null : log index {}", i.getIndex());
                    }
                    if (i.getDiffPressure() != null) {
                        diffpressureList.add(i.getDiffPressure());
                    } else {
                        log.debug("Diff Pressure ROP null : log index {}", i.getIndex());
                    }
                    if (i.getMudFlowInAvg() != null) {
                        mudFlowList.add(i.getMudFlowInAvg());
                    } else {
                        log.debug("Mud Flow ROP null : log index {}", i.getIndex());
                    }
                    if (i.getPumpPressure() != null) {
                        pumpPressureList.add(i.getPumpPressure());
                    } else {
                        log.debug("Pump pressure ROP null : log index {}", i.getIndex());
                    }
                    if (i.getSurfaceTorqueMax() != null) {
                        surfaceTorqueMaxList.add(i.getSurfaceTorqueMax());
                    } else {
                        log.debug("Surface Torque null : log index {}", i.getIndex());
                    }
                    if (i.getWeightonBitMax() != null) {
                        weightonBitMaxList.add(i.getWeightonBitMax());
                    } else {
                        log.debug("Weight On Bit Max null : log index {}", i.getIndex());
                    }
                });


           /* Map<Double, DepthLogReponse> depthMap = bcwDepthLog.parallelStream().collect(Collectors.toMap(DepthLogReponse::getHoleDepth, Function.identity()));

            LongStream.rangeClosed(rangeStart, rangeEnd)
                .map(i -> i * 100)
                .map(i -> {
                        bcwDepthLog.stream()
                            .filter(depth -> depth.getHoleDepth() >= i - 100 && depth.getHoleDepth() < i)
                            .collect(Collectors.summarizingDouble(DepthLogReponse::getHoleDepth));
                        return i;
                    }
                )
                .forEach(System.out::println);*/

        }

        return smoothedBCWData;

    }

    public class DepthClass {
        public Double holeDepth = -1d;
        public long holeDepthFirstRound = -1;
        public long holeDepthNextRound = -1;
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
            bcwDepthLogObject.setROPAvg(sum / count);
        }

        if (!rpmaList.isEmpty()) {
            final Double sum = rpmaList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = rpmaList.size();
            bcwDepthLogObject.setRpmaCount(count);
            bcwDepthLogObject.setRpmaSum(sum.floatValue());
            bcwDepthLogObject.setRpma(sum / count);
        }

        if (!diffpressureList.isEmpty()) {
            final Double sum = diffpressureList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = diffpressureList.size();
            bcwDepthLogObject.setDiffPressureCount(count);
            bcwDepthLogObject.setDiffPressureSum(sum.floatValue());
            bcwDepthLogObject.setDiffPressure(sum / count);
        }

        if (!mudFlowList.isEmpty()) {
            final Double sum = mudFlowList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = mudFlowList.size();
            bcwDepthLogObject.setMudFlowCount(count);
            bcwDepthLogObject.setMudFlowSum(sum.floatValue());
            bcwDepthLogObject.setMudFlowInAvg(sum / count);
        }

        if (!pumpPressureList.isEmpty()) {
            final Double sum = pumpPressureList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = pumpPressureList.size();
            bcwDepthLogObject.setPumpPressureCount(count);
            bcwDepthLogObject.setPumpPressureSum(sum.floatValue());
            bcwDepthLogObject.setPumpPressure(sum / count);

        }

        if (!surfaceTorqueMaxList.isEmpty()) {
            final Double sum = surfaceTorqueMaxList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = surfaceTorqueMaxList.size();
            bcwDepthLogObject.setSurfaceTorqueCount(count);
            bcwDepthLogObject.setSurfaceTorqueSum(sum.floatValue());
            bcwDepthLogObject.setSurfaceTorqueMax(sum / count);
        }

        if (!weightonBitMaxList.isEmpty()) {
            final Double sum = weightonBitMaxList.stream().mapToDouble(Double::doubleValue).sum();
            final int count = weightonBitMaxList.size();
            bcwDepthLogObject.setWeightOnBitCount(count);
            bcwDepthLogObject.setWeightOnBitSum(sum.floatValue());
            bcwDepthLogObject.setWeightonBitMax(sum / count);
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

    private List<DepthLogReponse> getOffSetLog1(List<DrillingRoadMapWells> offSetList, int lastOffSetEndIndex, int lastOffSetStartIndex) {

        List<LogDataRequestDTO> requestDTOList = setStartAndEndIndex(offSetList, lastOffSetEndIndex, lastOffSetStartIndex);

        List<List<DepthLogReponse>> logList = requestDTOList.parallelStream()
            .map(ls -> {
                    List<DepthLogReponse> logData = getLogData1("depth", ls.getWellUID(), String.valueOf(ls.getStartIndex()), String.valueOf(ls.getEndIndex()), 1000, false, true, null);
                    try {
                        logData = logData.parallelStream().filter(
                            k -> k.getHoleDepth() >= ls.getStartIndex()
                                && k.getHoleDepth() <= ls.getEndIndex())
                            .collect(Collectors.toList());
                        //logData.parallelStream().forEach(map -> map.put("offSetWellUid", ls.getWellUID()));
                        return logData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        List<DepthLogReponse> logData1 = new ArrayList<>();
                        return logData1;
                    }
                }
            ).collect(Collectors.toList());

        List<DepthLogReponse> consolidateList = logList.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        logList.size();

        return consolidateList;
    }

    private List<LogDataRequestDTO> setStartAndEndIndex(List<DrillingRoadMapWells> offSetList, int lastOffSetEndIndex, int lastOffSetStartIndex) {
        List<LogDataRequestDTO> requestDTOList = new LinkedList<>();
        log.info(" StartIndex: " + lastOffSetStartIndex + " EndIndex: " + lastOffSetEndIndex);
        for (int listIndex = 0; listIndex < offSetList.size(); listIndex++) {
            DrillingRoadMapWells obj = offSetList.get(listIndex);
            DrillingRoadMapWells obj1 = listIndex + 1 == offSetList.size() ? obj : offSetList.get(listIndex + 1);
            log.info("obj.getWellUid() " + obj.getWellUid() + " MD: " + obj.getMD() + "  obj1.getWellUid(): " + obj1.getWellUid() + "  MD: " + obj1.getMD());
            LogDataRequestDTO logDataRequestDTO = new LogDataRequestDTO();
            logDataRequestDTO.setWellUID(obj.getWellUid());
            if (listIndex == 0) {
                if (listIndex == offSetList.size() - 1) {
                    logDataRequestDTO.setStartIndex(lastOffSetStartIndex);
                    logDataRequestDTO.setEndIndex(lastOffSetEndIndex);
                } else {
                    logDataRequestDTO.setStartIndex(NumberParserUtils.intParse(obj.getMD()) < lastOffSetStartIndex ? lastOffSetStartIndex : NumberParserUtils.intParse(obj.getMD()));
                    logDataRequestDTO.setEndIndex(NumberParserUtils.intParse(obj1.getMD()));
                }
            } else if (listIndex == offSetList.size() - 1) {
                logDataRequestDTO.setStartIndex(NumberParserUtils.intParse(obj.getMD()));
                logDataRequestDTO.setEndIndex(lastOffSetEndIndex);
            } else {
                logDataRequestDTO.setStartIndex(NumberParserUtils.intParse(obj.getMD()));
                logDataRequestDTO.setEndIndex(NumberParserUtils.intParse(obj1.getMD()));

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

    private List<DepthLogReponse> getLogData1(String type, String wellUid, String startIndex, String endIndex,
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
        List<DepthLogReponse> logObj = null;
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
