package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.domain.mongo.WellPerformanceMetaData;
import com.moblize.ms.dailyops.dto.TrueRopCache;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponseV2;
import com.moblize.ms.dailyops.repository.mongo.client.WellPerformanceMetaDataRepository;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class CacheService {
    @Autowired
    RemoteCacheManager cacheManager;
    @Autowired
    @Lazy
    WellsCoordinatesService wellsCoordinatesService;
    @Autowired
    TrueRopCacheListener trueRopCacheListener;
    @Autowired
    private WellPerformanceMetaDataRepository metaDataRepository;
    @Autowired
    private RestClientService restClientService;
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Value("${CODE}")
    String COMPANY_NAME;
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void subscribe() {
        getWellCoordinatesCache().clear();
        log.debug("Cache service start");
        metaDataRepository.findAll().stream().forEach(metaData -> {
            processBuildAndWalk(metaData);
        });
        log.debug("Cache service end");
        wellsCoordinatesService.getWellCoordinates(COMPANY_NAME, null);
        getTrueRopMetaCache().addClientListener(trueRopCacheListener);
    }

    private void processBuildAndWalk(WellPerformanceMetaData metaData) {
        try {
            if (metaData.getBldWlkMeasureDepth() == 0 || metaData.getBldWlkMetaData().isEmpty()) {
                log.debug("Cache service process well {}", metaData.getWellUid());
                MongoWell mongoWell = mongoWellRepository.findByUid(metaData.getWellUid());
                    if(mongoWell != null){
                        restClientService.processWell(mongoWell);
                    }
            }
        } catch (Exception e) {
            log.error("Error occur in recalculate performance B&W ", e);
        }
    }


    public RemoteCache<String, TrueRopCache> getTrueRopMetaCache() {
        RemoteCache<String, TrueRopCache> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_WellRopDepth", DefaultTemplate.DIST_ASYNC);
        return  cache;
    }
    public RemoteCache<String, MongoWell> getMongoWellCache() {
        RemoteCache<String, MongoWell> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_MongoWell", DefaultTemplate.DIST_ASYNC);
        return cache;
    }

    public RemoteCache<String, WellCoordinatesResponseV2> getWellCoordinatesCache() {
        RemoteCache<String, WellCoordinatesResponseV2> cache = cacheManager.administration()
            .getOrCreateCache(COMPANY_NAME + "_WellCoordinates", DefaultTemplate.DIST_ASYNC);
        return cache;
    }
}
