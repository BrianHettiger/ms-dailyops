package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.PerformanceWellDao;
import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PerformanceWellService {
    @Autowired
    private PerformanceWellDao performanceWellDao;
    @Autowired
    private CacheService cacheService;

    public PerformanceWell savePerformanceWell(PerformanceWell performanceWellDTO){
        return performanceWellDao.saveUpdatePerformanceWell(performanceWellDTO);
    }

    public PerformanceWell updatePerformanceWell(PerformanceWell performanceWellDTO){
        return performanceWellDao.saveUpdatePerformanceWell(performanceWellDTO);
    }

    public PerformanceWell findPerformanceWell(String uid) {
        return performanceWellDao.findPerformanceWell(uid);
    }

    public void deletePerformanceWell(String uid){
        cacheService.getTrueRopMetaCache().remove(uid);
        cacheService.getWellCoordinatesCache().remove(uid);
        cacheService.getMongoWellCache().remove(uid);
        performanceWellDao.deletePerformanceWell(uid);
    }
}
