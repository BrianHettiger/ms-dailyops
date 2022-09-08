package com.moblize.ms.dailyops.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.client.WitsmlLogsClient;
import com.moblize.ms.dailyops.domain.FormationMarker;
import com.moblize.ms.dailyops.utils.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DrillingRoadmapDAO {

    @Autowired
    WitsmlLogsClient witsmlLogsClient;

    public List<FormationMarker> getFormationMarkers(List<String> wellUid, String wellboreUid) {
        List<FormationMarker> formationMarkers = Collections.emptyList();
        try {
            // Call for the Record Set
            JSONResult jsonResult = witsmlLogsClient.getFormationMarkersForWells(wellUid,wellboreUid);
            log.info(jsonResult.status);
            log.info(jsonResult.data.toString());
            final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
            JavaType type = mapper.getTypeFactory().
                constructCollectionType(List.class, FormationMarker.class);
            formationMarkers = mapper.convertValue(jsonResult.data, type);

            Collections.sort(formationMarkers, new Comparator<FormationMarker>() {
                @Override
                public int compare(FormationMarker o1, FormationMarker o2) {
                    if (o1.getTVD() == null) {
                        return -1;
                    }
                    else if (o2.getTVD() == null) {
                        return 1;
                    }
                    else {
                        return o1.getTVD().compareTo(o2.getTVD());
                    }
                }
            });
        } catch (Exception iae) {
            log.error("Error occur in FormationMarkerClient.getFormationMarkers API call ", iae);
        }

        return formationMarkers;
    }


    public Map<String, List<FormationMarker>> formationMarkersForAllWells(List<String> wellUidList) {
        Map<String, List<FormationMarker>> formationMarkers = new HashMap<>();
        String wellboreUid = "Wellbore1";
        try {
            if (wellUidList.size() > 0) {
                List<FormationMarker> dataList = getFormationMarkers(wellUidList, wellboreUid);
                formationMarkers = dataList.stream().collect(Collectors.groupingBy(FormationMarker::getWellUid));
            }
        } catch (Exception e) {
            log.error("Error in getFormationMarkersForOffset", e);
        }
        return formationMarkers;
    }


}
