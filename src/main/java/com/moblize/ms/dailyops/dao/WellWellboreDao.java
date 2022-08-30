package com.moblize.ms.dailyops.dao;


import com.moblize.ms.dailyops.domain.Wellbore;
import com.moblize.ms.dailyops.repository.GenericCustomRepository;
import com.moblize.ms.dailyops.repository.HoleSectionRepository;
import com.moblize.ms.dailyops.repository.WellboreRepository;
import com.moblize.ms.dailyops.service.dto.HoleSection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
@Component
public class WellWellboreDao {

    @Autowired
    private GenericCustomRepository genericCustomRepository;
    @Autowired
    private WellboreRepository wellboreRepository;
    @Autowired
    private HoleSectionRepository holeSectionRepository;

    private static Map<String, Wellbore> wellUidToWellbore = new HashMap<>();

    public  Long getWellobreIdFromWellUid(String wellUid)
    {
        Wellbore wellbore = wellUidToWellbore.get(wellUid);
        if (wellbore == null)
        {
            Wellbore searchWellbore  = wellboreRepository.findFirstByWellUid(wellUid);
            if (searchWellbore != null)
            {
                wellUidToWellbore.put(wellUid, searchWellbore);
                wellbore = searchWellbore;
            }
        }

        if (wellbore == null) {
            return null;
        }

        return wellbore.getId();
    }

    public  Set<Long> getWellobreIdFromWellUid(Set<String> wellUid) {

        if(wellUid.isEmpty())
            return Collections.emptySet();

        final Query query = genericCustomRepository.find("SELECT w.id FROM Wellbore w where w.wellUid = wellUid");
        query.setParameter("wellUid", wellUid);

        return new HashSet<>(query.getResultList());
    }

    public Wellbore get(String wellUid) {
        return wellUidToWellbore.get(wellUid);
    }

    public void put(String wellUid, Wellbore wellbore) {
        wellUidToWellbore.put(wellUid, wellbore);
    }

    public List<HoleSection> getHolSectionsList(Collection<Long> wellBoreId) {
        if (wellBoreId.isEmpty())
            return Collections.emptyList();
        return holeSectionRepository.findAllByWellboreIdIn(wellBoreId);
    }
}
