package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.PerformanceBHADao;
import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PerformanceBHAService {
    @Autowired
    private PerformanceBHADao performanceBHADao;

    public PerformanceBHA savePerformanceBHA(PerformanceBHA performanceBHADTO){
        return performanceBHADao.saveUpdatePerformanceBHA(performanceBHADTO);
    }

    public PerformanceBHA updatePerformanceBHA(PerformanceBHA performanceBHADTO){
        return performanceBHADao.saveUpdatePerformanceBHA(performanceBHADTO);
    }

    public PerformanceBHA findPerformanceBHA(String uid) {
        return performanceBHADao.findPerformanceBHA(uid);
    }

    public void deletePerformanceBHA(String uid){
        performanceBHADao.deletePerformanceBHA(uid);
    }
}
