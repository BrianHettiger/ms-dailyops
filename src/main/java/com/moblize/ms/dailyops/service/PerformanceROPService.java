package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.PerformanceROPDao;
import com.moblize.ms.dailyops.domain.PerformanceROP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PerformanceROPService {
    @Autowired
    private PerformanceROPDao performanceROPDao;

    public PerformanceROP savePerformanceROP(PerformanceROP performanceROPDTO){
        return performanceROPDao.savePerformanceROP(performanceROPDTO);
    }

    public PerformanceROP updatePerformanceROP(PerformanceROP performanceROPDTO){
        return performanceROPDao.updatePerformanceROP(performanceROPDTO);
    }

    public PerformanceROP findPerformanceROP(String uid) {
        return performanceROPDao.findPerformanceROP(uid);
    }

    public void deletePerformanceROP(String uid){
        performanceROPDao.deletePerformanceROP(uid);
    }
}
