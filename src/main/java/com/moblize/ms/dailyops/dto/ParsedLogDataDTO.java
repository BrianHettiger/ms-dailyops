package com.moblize.ms.dailyops.dto;

import com.moblize.ms.dailyops.utils.Convert;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
public class ParsedLogDataDTO implements Serializable {
    private static final String INDEX_PROP_NAME = "index";
    private static final String RIG_STATE_PROP_NAME = "RigState";

    private String index;
    private String rigState;
    private String logType;

    private Map<String, Object> channels;

    public ParsedLogDataDTO(Map<String, Object> all) {
        this(all, null);
    }

    public ParsedLogDataDTO(Map<String, Object> all, String logType) {
        this.logType = logType;
        index = String.valueOf(all.get(INDEX_PROP_NAME));
        all.remove(INDEX_PROP_NAME);
        rigState = all.get(RIG_STATE_PROP_NAME).toString();
        all.remove(RIG_STATE_PROP_NAME);
        channels = new HashMap<>();
        channels.putAll(all);
    }

    public String getRigState() {
        return rigState;
    }

    public Number getIndex() {
        if ("time".equalsIgnoreCase(logType)) {
            return Long.valueOf(index);
        }
        return Double.parseDouble(index);
    }

    public Double get(String channelName) {
        return getChannelValueAsDouble(channelName);
    }

    public Double getChannelValueAsDouble(String channelName) {
        return Convert.tryDoubleParse(String.valueOf(channels.get(channelName)));
    }

    public Float getChannelValueAsFloat(String channelName) {
        return Convert.tryFloatParse(String.valueOf(channels.get(channelName)));
    }

    public String getChannelValueAsString(String channelName) {
        if (null != channels.get(channelName)) {
            return String.valueOf(channels.get(channelName));
        }
        return null;
    }

    public boolean containsKey(String channelName) {
        return channels.containsKey(channelName);
    }

    public Set<String> keySet() {
        return channels.keySet();
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public Map<String, Object> getChannels() {
        return channels;
    }
}
