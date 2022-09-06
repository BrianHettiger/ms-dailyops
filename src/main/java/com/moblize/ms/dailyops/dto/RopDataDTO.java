package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
@ToString
public class RopDataDTO {

    @JsonProperty("groupNames")
    private List<String> groupNames = new ArrayList<>();
    @JsonProperty("viewData")
    private HashMap<String, Object> viewData = new HashMap<>();
    @JsonProperty("mdRange")
    private Map<String, Map<String, Map<String, Number>>> mdRange = new HashMap<>();

    public void add(String ropType, String groupId, Object value) {
        HashMap<String, Object> groupMap = new HashMap<>();
        groupMap.put(groupId, value);
        if (viewData == null) {
            viewData = new HashMap<>();
        }
        viewData.put(ropType, groupMap);
    }

    public void addMdRange(String ropType, String groupId, Map<String, Number> value) {
        final Map<String, Map<String, Number>> groupMap = new HashMap<>();
        groupMap.put(groupId, value);
        if (mdRange == null) {
            mdRange = new HashMap<>();
        }
        mdRange.put(ropType, groupMap);
    }

    public void append(String ropType, String groupId, Object value) {
        HashMap<String, Object> groupMap;
        if (viewData == null) {
            viewData = new HashMap<>();
            groupMap = new HashMap<>();
            groupMap.put(groupId, value);
            viewData.put(ropType, groupMap);
        } else {
            groupMap = (HashMap<String, Object>)viewData.get(ropType);
            if (groupMap == null) {
                groupMap = new HashMap<>();
                groupMap.put(groupId, value);
                viewData.put(ropType, groupMap);
            }
            else {
                groupMap.put(groupId, value);
            }
        }
    }

    public void appendMdRange(String type, String groupId, Map<String, Number> value) {
        if (mdRange == null) {
            mdRange = new HashMap<>();
            final Map<String, Map<String, Number>> groupMap = new HashMap<>();
            groupMap.put(groupId, value);
            mdRange.put(type, groupMap);
        } else {
            Map<String, Map<String, Number>> groupMap = mdRange.get(type);
            if(null == groupMap) {
                groupMap = new HashMap<>();
                groupMap.put(groupId, value);
                mdRange.put(type, groupMap);
            } else {
                groupMap.put(groupId, value);
            }
        }

    }
}
