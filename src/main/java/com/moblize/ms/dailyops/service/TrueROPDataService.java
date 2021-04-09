package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.TrueROPDataDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrueROPDataService {

    @Autowired
    private TrueROPDataDao trueROPDataDao;

    public Long getLastProcessUpTo(String wellUid){
        return  trueROPDataDao.getLastProcessUpTo(wellUid);
    }
}
