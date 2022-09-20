package com.moblize.ms.dailyops.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.ms.dailyops.domain.FormationMarker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DrillingRoadMapFormationBuilder {


    @Autowired
    private DrillingRoadmapDAO drillingRoadMapDao;
    @Autowired
    private ObjectMapper objectMapper;
    /**
     * getFormationMap formation map that has primary well and matching offset wells
     *
     * @param primaryWellUid
     * @param offsetWellUids
     * @return
     */
    public Map<String, List<FormationMarker>> getFormationMap(String primaryWellUid, List<String> offsetWellUids, String wellboreUid) {
        Map<String, List<FormationMarker>> wellFormationMap = new HashMap<>();
        try {
            List<String> allWellUid = new ArrayList<>(offsetWellUids);
            allWellUid.add(primaryWellUid);
            log.info("allWellUid: {}", objectMapper.writeValueAsString(allWellUid));
            Map<String, List<FormationMarker>> formationMarkersForAllWells= drillingRoadMapDao.formationMarkersForAllWells(allWellUid);
            log.info("formationMarkersForAllWells: {}", objectMapper.writeValueAsString(formationMarkersForAllWells));

            final List<FormationMarker> primaryWellFormationList =
                formationMarkersForAllWells.remove(primaryWellUid).stream()
                    .filter(formationMarker -> formationMarker.getMD() != null && formationMarker.getMD() == 0.0)
                    .collect(Collectors.toList());
            sortOffsetWellFormationByMD(primaryWellFormationList);
            formationMarkersForAllWells.forEach((wellUid, offsetWellFormations) -> {
                final List<FormationMarker> matchingFormationList = new ArrayList<>();
                primaryWellFormationList.forEach(primaryWellFormation-> {
                    final String primaryWellFormationName = WordUtils.capitalizeFully(primaryWellFormation.getName().trim());
                    primaryWellFormation.setName(WordUtils.capitalizeFully(primaryWellFormationName));
                    offsetWellFormations.forEach(offsetWellFormation -> {
                        String offsetWellFormationName = offsetWellFormation.getName().trim();
                        if (offsetWellFormationName.equalsIgnoreCase(primaryWellFormationName)) {
                            offsetWellFormation.setName(primaryWellFormationName);
                            matchingFormationList.add(offsetWellFormation);
                        }
                    });
                });
                //Ignoring wells with matching one formation ,as that will not be a part of calculation.
                if (matchingFormationList.size() > 1) {
                    try {
                        log.info("matchingFormationList: {}", objectMapper.writeValueAsString(matchingFormationList));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    sortOffsetWellFormationByMD(matchingFormationList);
                    wellFormationMap.put(wellUid, matchingFormationList);
                }
            });

            wellFormationMap.put(primaryWellUid, primaryWellFormationList);
            log.info("wellFormationMap: {}", objectMapper.writeValueAsString(wellFormationMap));

        } catch (Exception exception) {
            log.error("Error while performing formation marker filtering [getFormationMarker]", exception);
        }
        return wellFormationMap;
    }

    private void sortOffsetWellFormationByMD(List<FormationMarker> matchingFormationList) {
        matchingFormationList.sort((FormationMarker f1, FormationMarker f2) -> {
            if (f1.getMD() != null && f2.getMD() != null) {
                return f1.getMD() == f2.getMD() ? 0 : ((f1.getMD() > f2.getMD()) ? 1 : -1);
            } else {
                return 0;
            }
        });
    }
}
