package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.mongo.MongoLog;
import com.moblize.ms.dailyops.utils.MetricsLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MongoLogService {

    @Autowired
    private WitsmlLogService logService;

    public MongoLog fillLogDataNoReduceNoUserUOMSettings(Float startIndex, Float endIndex, MongoLog mongoLog) {
        long startTime = System.currentTimeMillis();
        mongoLog.setParsedLogData(
            logService.getDataRowsWithLimitNoUserUOMSettings(mongoLog, startIndex.longValue(), endIndex.longValue(), -1, true, null ));
        MetricsLogger.nodeDrillingLogsRate(
            "query", startTime, System.currentTimeMillis(), mongoLog.getParsedLogData().size());
        return mongoLog;
    }

}
