package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import com.moblize.ms.dailyops.repository.mongo.mob.MongoWellRepository;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@ClientListener
@Slf4j
@Component
public class TrueRopCacheListener {
    @Autowired
    private MongoWellRepository mongoWellRepository;
    @Autowired
    RestClientService restClientService;
    @ClientCacheEntryCreated
    public void entryCreated(ClientCacheEntryCreatedEvent<String> event) {
        updateData(event.getKey());
    }
    @ClientCacheEntryModified
    public void entryModified(ClientCacheEntryModifiedEvent<String> event) {
        updateData(event.getKey());
    }

    public void updateData(String key) {
        String wellUid = key;
        MongoWell mongoWell = mongoWellRepository.findByUid(key);
        log.debug("processWell {}", wellUid);
        restClientService.
            processWell(mongoWell);
    }


}
