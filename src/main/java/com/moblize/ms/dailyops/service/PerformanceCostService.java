package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.PerformanceCostDao;
import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PerformanceCostService {
    @Autowired
    private PerformanceCostDao performanceCostDao;

    public PerformanceCost savePerformanceCost(PerformanceCost performanceCostDTO){
        return performanceCostDao.savePerformanceCost(performanceCostDTO);
    }

    public PerformanceCost updatePerformanceCost(PerformanceCost performanceCostDTO){
        return performanceCostDao.updatePerformanceCost(performanceCostDTO);
    }

    public PerformanceCost findPerformanceCost(String uid) {
        return performanceCostDao.findPerformanceCost(uid);
    }

    public void deletePerformanceCost(String uid){
        performanceCostDao.deletePerformanceCost(uid);
    }
}
