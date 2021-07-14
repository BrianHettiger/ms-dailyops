package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.dao.WellFormationDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DrillingRoadmapService {

    @Autowired
    WellFormationDAO wellFormationDAO;

    public void getBCWListForOffsetWell(){

    }
}
