package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.FormationMarker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DrillingRoadMapFormationBuilder {


    @Autowired
    private DrillingRoadmapDAO drillingRoadMapDao;

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
            Map<String, List<FormationMarker>> formationMarkersForAllWells= drillingRoadMapDao.formationMarkersForAllWells(allWellUid);

            List<FormationMarker> primaryWellFormationList = new ArrayList<>();
            if(formationMarkersForAllWells.containsKey(primaryWellUid)){
                List<FormationMarker> primaryWellFormationListTemp = formationMarkersForAllWells.get(primaryWellUid);
                formationMarkersForAllWells.remove(primaryWellUid);
                primaryWellFormationListTemp.stream().
                    filter(formationMarker -> formationMarker.getMD() == null || formationMarker.getMD() == 0.0).findFirst().
                    ifPresent(formationMaker -> {
                        primaryWellFormationListTemp.remove(formationMaker);
                    });
                primaryWellFormationList.addAll(primaryWellFormationListTemp);
                sortOffsetWellFormationByMD(primaryWellFormationList);
            }
            formationMarkersForAllWells.keySet().stream().map(wellUid -> {
                List<FormationMarker> matchingFormationList = new ArrayList<>();
                List<FormationMarker> offsetWellFormations = formationMarkersForAllWells.get(wellUid);
                    primaryWellFormationList.stream().map(primaryWellFormation->{
                    String primaryWellFormationName = primaryWellFormation.getName().trim();
                    primaryWellFormation.setName(WordUtils.capitalizeFully(primaryWellFormationName));
                    for (FormationMarker offsetWellFormation : offsetWellFormations) {
                        String offsetWellFormationName = offsetWellFormation.getName().trim();
                        if (offsetWellFormationName.equalsIgnoreCase(primaryWellFormationName)) {
                            offsetWellFormation.setName(WordUtils.capitalizeFully(primaryWellFormationName));
                            matchingFormationList.add(offsetWellFormation);
                            break;
                        }
                    }
                    return null;
                });
                //Ignoring wells with matching one formation ,as that will not be a part of calculation.
                if (matchingFormationList.size() > 1) {
                    sortOffsetWellFormationByMD(matchingFormationList);
                    wellFormationMap.put(wellUid, matchingFormationList);
                }
                return null;
            });

            wellFormationMap.put(primaryWellUid, primaryWellFormationList);

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
