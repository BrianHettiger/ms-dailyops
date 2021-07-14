package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.BCWDepthLogDao;
import com.moblize.ms.dailyops.domain.mongo.BCWDepthLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BCWDepthLogService {

    @Autowired
    private BCWDepthLogDao bcwDepthLogDao;

    public BCWDepthLog saveUpdateBCWDepthLog(BCWDepthLog bcwDepthLog){
        return bcwDepthLogDao.saveUpdateBCWDepthLog(bcwDepthLog);
    }
}
