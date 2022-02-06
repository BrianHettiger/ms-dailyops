package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.repository.mongo.client.TargetWindowDPVARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class TargetWindowDPVAService {

    @Autowired
    private TargetWindowDPVARepository targetWindowDPVARepository;



    public TargetWindowDPVA getTargetWindowDetail(String wellUID) {
        TargetWindowDPVA targetWindowDPVA = null;
        try {
            targetWindowDPVA = targetWindowDPVARepository.findFirstByUid(wellUID);
            if (targetWindowDPVA == null) {
                targetWindowDPVA = new TargetWindowDPVA();
                targetWindowDPVA.setUid(wellUID);
                targetWindowDPVA.setIsEnable(true);
                targetWindowDPVA.setSelectedMode("basic");
                TargetWindowDPVA.SectionView section = new TargetWindowDPVA.SectionView(20);
                TargetWindowDPVA.PlanView plan = new TargetWindowDPVA.PlanView(50);
                TargetWindowDPVA.Basic basic = new TargetWindowDPVA.Basic(section, plan);
                targetWindowDPVA.setBasic(basic);
                targetWindowDPVA.setAdvance(new ArrayList<>());

                targetWindowDPVA = targetWindowDPVARepository.save(targetWindowDPVA);
            }
        } catch (Exception e) {
            log.error("Error occur in getTargetWindowDetail for well uid: {}", wellUID, e);
        }
        return targetWindowDPVA;
    }

    public TargetWindowDPVA saveTargetWindowDetail(TargetWindowDPVA targetWindow, String wellStatus) {
        TargetWindowDPVA Obj = null;
        try {
            TargetWindowDPVA targetWindowDB = getTargetWindowDetail(targetWindow.getUid());
            if (null != targetWindowDB) {
                targetWindowDB.setIsEnable(targetWindow.getIsEnable());
                targetWindowDB.setSelectedMode(targetWindow.getSelectedMode());
                targetWindowDB.setAdvance(targetWindow.getAdvance());
                targetWindowDB.setBasic(targetWindow.getBasic());
                Obj = targetWindowDPVARepository.save(targetWindowDB);
            } else {
                Obj = targetWindowDPVARepository.save(targetWindow);
            }

        } catch (Exception e) {
            log.error("Error occur in saveTarget window service for well uid: {}", targetWindow.getUid(), e);
        }
        return Obj;
    }
}
