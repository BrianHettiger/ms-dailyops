package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.dto.ParsedLogDataDTO;
import com.moblize.ms.dailyops.dto.RopModelWell;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
@Document(collection = "logs")
@AllArgsConstructor
@NoArgsConstructor
public class MongoLog {
    public static final String INDEX_COLUMN_NAME = "log_max_val";
    private static DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    private static ObjectMapper objectMapper = new ObjectMapper();

    public enum LogType {
        ALL("All"),
        DATE_TIME("Time Log"),
        MEASURED_DEPTH("Depth Log");

        private String msg;

        LogType(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

    private String uidWell;
    private Float startIndex;
    private Float endIndex;
    private String indexType;
    private String uidWellbore;
    @Transient
    private String minIndex;
    @Transient
    private String maxIndex;

    @Id
    private String id;

    private List<LogCurveInfo> logCurveInfos = new ArrayList<>();

    @Transient
    private List<ParsedLogDataDTO> parsedLogData = new ArrayList<>();

    @Transient
    private Map<String, String> logDataUnits;

    @Transient
    private RopModelWell minMseModelWell;
    @Transient
    private RopModelWell minMseModelWell2;
    @Transient
    private Integer sampling;


    public Map<String, String> getLogDataUnits() {
        if (logDataUnits == null) {
            logDataUnits = populateLogDataUnits();
        }
        return logDataUnits;
    }

    @Getter
    @Setter
    public static class LogCurveInfo implements Serializable {
        private String mnemonic;
        private Integer columnIndex;
        private String curveDescription;
        private String typeLogData;
        private String unit;
    }

    public Map<String, String> populateLogDataUnits() {

        Map<String, String> logDataUnits = new HashMap<>();

        for (MongoLog.LogCurveInfo curveInfo : getLogCurveInfos()) {
            String channelName = curveInfo.getMnemonic();
            String unit = curveInfo.getUnit() != null ? curveInfo.getUnit() : "";
            logDataUnits.put(channelName, unit);
        }
        return logDataUnits;
    }

    public String getSimpleType() {
        return getIndexType().equals("DATE_TIME") ? "time" : "depth";
    }
}
