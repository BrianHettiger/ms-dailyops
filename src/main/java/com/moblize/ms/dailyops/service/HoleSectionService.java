package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.Wellbore;
import com.moblize.ms.dailyops.repository.HoleSectionRepository;
import com.moblize.ms.dailyops.repository.WellboreRepository;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Service
public class HoleSectionService {
    @Autowired
    private HoleSectionRepository holeSectionRepository;
    @Autowired
    private WellboreRepository wellboreRepository;

    public List<HoleSection> getHoleSections(String wellUid){
        Wellbore wellbore = wellboreRepository.findFirstByWellUid(wellUid);
        if(wellbore == null) {
            return null;
        }
        Long wellboreId = wellbore.getId();
        List<HoleSection> sections = holeSectionRepository.findByWellboreId(wellboreId);

        return sections;
    }

    public HoleSection.HoleSectionType getHoleSection(Float holeDepth, String wellUid) {

        List<HoleSection> holeSections = getHoleSections(wellUid);
        HoleSection firstHoleSection = holeSections.get(0);
        HoleSection lastHoleSection = holeSections.get(holeSections.size() - 1);

        if (holeDepth < firstHoleSection.getFromDepth()) {
            return HoleSection.HoleSectionType.UNKNOWN;
        } else if (holeDepth > lastHoleSection.getToDepth()) {
            return HoleSection.HoleSectionType.UNKNOWN;
        } else {
            Optional<HoleSection> holeSection = holeSections.stream().filter(obj -> obj.getFromDepth() > holeDepth).findFirst();
            if(holeSection.isPresent()){
                return holeSection.get().getSection();
            }
        }
        return lastHoleSection.getSection();
    }
}
