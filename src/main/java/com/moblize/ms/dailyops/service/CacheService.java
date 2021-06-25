package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.dto.TrueRopCache;
import com.moblize.ms.dailyops.dto.WellCoordinatesResponseV2;
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
    @Lazy
    TrueRopCacheListener trueRopCacheListener;

    @Value("${CODE}")
    String COMPANY_NAME;
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void subscribe() {
        wellsCoordinatesService.getWellCoordinates(COMPANY_NAME);
        getTrueRopMetaCache().addClientListener(trueRopCacheListener);
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
