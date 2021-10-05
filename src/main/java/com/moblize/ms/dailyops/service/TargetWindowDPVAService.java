package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.client.KpiDashboardClient;
import com.moblize.ms.dailyops.domain.mongo.TargetWindowDPVA;
import com.moblize.ms.dailyops.repository.mongo.client.TargetWindowDPVARepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class TargetWindowDPVAService {

    @Autowired
    private TargetWindowDPVARepository targetWindowDPVARepository;

    @Autowired
    private NotifyDPVAService notifyDPVAService;



    public TargetWindowDPVA getTargetWindowDetail(String wellUID) {
        TargetWindowDPVA targetWindowDPVA = null;
        try {
            targetWindowDPVA = targetWindowDPVARepository.findFirstByUid(wellUID);
            if (targetWindowDPVA == null) {
                targetWindowDPVA = new TargetWindowDPVA();
                targetWindowDPVA.setUid(wellUID);
                targetWindowDPVA.setIsEnable(false);
                targetWindowDPVA.setSelectedMode("basic");
                TargetWindowDPVA.SectionView section = new TargetWindowDPVA.SectionView(15);
                TargetWindowDPVA.PlanView plan = new TargetWindowDPVA.PlanView(30);
                TargetWindowDPVA.Basic basic = new TargetWindowDPVA.Basic(section, plan);
                targetWindowDPVA.setBasic(basic);
                targetWindowDPVA.setAdvance(new ArrayList<>());

                targetWindowDPVA = targetWindowDPVARepository.save(targetWindowDPVA);
            }
        } catch (Exception e) {
            log.error("Error occur in getTargetWindowDetail", e);
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
            notifyDPVAService.notifyDPVAJobForSaveTargetWindow(targetWindow, wellStatus);
        } catch (Exception e) {
            log.error("Error occur in saveTarget window service", e);
        }
        return Obj;
    }
}
