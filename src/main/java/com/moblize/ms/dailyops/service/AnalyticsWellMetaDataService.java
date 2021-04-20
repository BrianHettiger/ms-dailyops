package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.AnalyticsWellMetaDataDao;
import com.moblize.ms.dailyops.domain.AnalyticsWellMetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnalyticsWellMetaDataService {

    @Autowired
    private AnalyticsWellMetaDataDao analyticsWellMetaDataDao;

    public Long getLastProcessUpTo(String wellUid){
        AnalyticsWellMetaData analyticsWellMetaData = analyticsWellMetaDataDao.getLastProcessUpTo(wellUid);
        if(null != analyticsWellMetaData && null != analyticsWellMetaData.getLastProcessUpTo()){
            return analyticsWellMetaData.getLastProcessUpTo();
        } else {
            return null;
        }

    }
}
