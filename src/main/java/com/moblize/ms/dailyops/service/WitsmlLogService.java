package com.moblize.ms.dailyops.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.dto.ParsedLogDataDTO;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoLogRepository;
import com.moblize.ms.dailyops.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WitsmlLogService {

    private static final Integer ASC = 1;
    private static final Integer DESC = -1;
    public static final int MAX_LOG_QUERY_DAYS = 40;
    public static final int DAYS_IN_YEAR = 360;
    public static final int UNLIMITED_CHUNK_SIZE = -1;
    private static final String DEFAULT_WELLBORE_ID = "Wellbore1";

    @Autowired
    private MongoLogRepository mobMongoLog;

    @Autowired
    private RestClientService restClientService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<MongoLog> getMongoWitsmlLogs(String wellUid) {
        return mobMongoLog.findByUidWell(wellUid);
    }

    public MongoLog getMongoWitmlDepthLog(String wellUid) {
        return mobMongoLog.findFirstByUidWellAndIndexType(wellUid, "MEASURED_DEPTH");
    }

    public List<ParsedLogDataDTO> getDataRowsWithLimitNoUserUOMSettings(
        MongoLog log,
        Long startL,
        Long endL,
        Integer limit,
        Boolean disableReduce,
        List<String> channels) {
        return getDataRowsWithLimitNoUserUOMSettings(
            log, startL, endL, limit, disableReduce, ASC, channels);
    }

    private List<ParsedLogDataDTO> getDataRowsWithLimitNoUserUOMSettings(
        MongoLog log,
        Long startL,
        Long endL,
        Integer limit,
        Boolean disableReduce,
        Integer sort,
        List<String> channels) {
        List<Map<String, Object>> recs =
            getParsedLogDatas(
                log.getSimpleType(),
                log.getUidWell(),
                startL.toString(),
                endL.toString(),
                limit,
                disableReduce,
                false,
                channels);
        List<ParsedLogDataDTO> ret = new ArrayList<>();
        if (sort.equals(ASC)) {
            for (Map<String, Object> each : recs) {
                ret.add(new ParsedLogDataDTO(each, log.getSimpleType()));
            }
        } else {
            for (int i = recs.size() - 1; i > -1; i--) {
                ret.add(new ParsedLogDataDTO(recs.get(i), log.getSimpleType()));
            }
        }
        return ret;
    }

    public List<Map<String, Object>> getParsedLogDatas(
        String type,
        String wellUid,
        String startIndex,
        String endIndex,
        Integer limit,
        Boolean disableReduce,
        Boolean useUserUOMSettings,
        List<String> channels) {
        List<Map<String, Object>> allObjs = null;
        if (type.equals("time")) {
            if (disableReduce && limit == -1) {
                throw new IllegalArgumentException(
                    "You can't disable reduction but get all the data. It will be harmful to the system.");
            }

            if (Long.parseLong(startIndex) < 946684800000L) { // if before 1/1/2000, let's not
                throw new IllegalArgumentException(
                    "You are requesting for data before 1/1/2000. Is this intentional? If so, please contact admin. The system currently does not allow that");
            }
            long startTime = Long.parseLong(startIndex);
            long endTime = Long.parseLong(endIndex);
            long daysBetween = DateUtil.daysBetween(startTime, endTime);
            if (daysBetween > MAX_LOG_QUERY_DAYS && disableReduce) {
                if (daysBetween > DAYS_IN_YEAR) {
                    endTime = DateUtil.addDays(startTime, DAYS_IN_YEAR).getTime();
                    endIndex = String.valueOf(endTime);
                    daysBetween = DateUtil.daysBetween(startTime, endTime);
                }
                log.debug(
                    "getParsedLogDatas - time range is too large, breaking into multiple requests for well "
                        + wellUid
                        + " start index "
                        + startIndex
                        + " end index"
                        + endIndex
                        + " days between "
                        + daysBetween);
                String queryStartTimeStr = startIndex;
                long queryEndTime = DateUtil.addDays(startTime, MAX_LOG_QUERY_DAYS).getTime();
                String queryEndTimeStr = String.valueOf(queryEndTime);
                boolean endTimeReached = false;
                long daysLeft = daysBetween - MAX_LOG_QUERY_DAYS;
                while (!endTimeReached) {
                    log.debug(
                        "getParsedLogDatas - one of multiple requests for well "
                            + wellUid
                            + " start index "
                            + queryStartTimeStr
                            + " end index"
                            + queryEndTimeStr
                            + " days left "
                            + daysLeft
                            + " days between "
                            + daysBetween
                            + " from: "
                            + startIndex
                            + " to: "
                            + endIndex);
                    List<Map<String, Object>> objs =
                        getLogData(
                            type,
                            wellUid,
                            queryStartTimeStr,
                            queryEndTimeStr,
                            limit,
                            disableReduce,
                            useUserUOMSettings,
                            channels);
                    if (allObjs == null) {
                        allObjs = objs;
                    } else {
                        allObjs.addAll(objs);
                    }
                    if (limit == UNLIMITED_CHUNK_SIZE || limit > objs.size()) {
                        queryStartTimeStr = queryEndTimeStr;
                        if (daysLeft > 0) {
                            if (daysLeft > MAX_LOG_QUERY_DAYS) {
                                queryEndTime = DateUtil.addDays(queryEndTime, MAX_LOG_QUERY_DAYS).getTime();
                                queryEndTimeStr = String.valueOf(queryEndTime);
                                daysLeft = daysLeft - MAX_LOG_QUERY_DAYS;
                            } else {
                                log.debug(
                                    "getParsedLogDatas - one of multiple requests for well "
                                        + wellUid
                                        + " start index "
                                        + queryStartTimeStr
                                        + " end index "
                                        + endIndex
                                        + " days left "
                                        + daysLeft
                                        + " days between "
                                        + daysBetween
                                        + " from: "
                                        + startIndex
                                        + " to: "
                                        + endIndex);
                                objs =
                                    getLogData(
                                        type,
                                        wellUid,
                                        queryStartTimeStr,
                                        endIndex,
                                        limit,
                                        disableReduce,
                                        useUserUOMSettings,
                                        channels);
                                allObjs.addAll(objs);
                                endTimeReached = true;
                            }
                        } else {
                            endTimeReached = true;
                        }
                    } else {
                        endTimeReached = true;
                    }
                }
            } else {
                log.debug(
                    "getParsedLogDatas - time range is normal, one request for well "
                        + wellUid
                        + " start index "
                        + startIndex
                        + " end index "
                        + endIndex
                        + " days between "
                        + daysBetween);
                allObjs =
                    getLogData(
                        type,
                        wellUid,
                        startIndex,
                        endIndex,
                        limit,
                        disableReduce,
                        useUserUOMSettings,
                        channels);
            }
        } else {
            allObjs =
                getLogData(
                    type,
                    wellUid,
                    startIndex,
                    endIndex,
                    limit,
                    disableReduce,
                    useUserUOMSettings,
                    channels);
        }

        return allObjs;
    }

    public List<Map<String, Object>> getLogData(
        String type,
        String wellUid,
        String startIndex,
        String endIndex,
        Integer limit,
        Boolean disableReduce,
        Boolean useUserUOMSettings,
        List<String> channels) {
        long startTime = System.currentTimeMillis();

        if (type.equals("time")) {

            if (disableReduce && limit == -1) {
                throw new IllegalArgumentException(
                    "You can't disable reduction but get all the data. It will be harmful to the system.");
            }

            if (Long.parseLong(startIndex) < 946684800000L) { // if before 1/1/2000, let's not
                throw new IllegalArgumentException(
                    "You are requesting for data before 1/1/2000. Is this intentional? If so, please contact admin. The system currently does not allow that");
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
        String userName = null;  //How do we get username in spring boot
        if(userName!= null && !userName.isEmpty()) {
            params.put("userName=", userName);
        }
        ResponseEntity<JsonNode> response = restClientService.getLatestCustomChannel(params);
        if(response.getStatusCode().is2xxSuccessful()) {
            return objectMapper.convertValue(response.getBody().get("data"), new ArrayList<Map<String, Object>>().getClass());
        } else {
            return null;
        }
    }
}
