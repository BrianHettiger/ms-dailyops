package com.moblize.ms.dailyops.service;

import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ClientListener
@Slf4j
@Component
public class SurveyDataCacheListener {

    @Autowired
    private NotifyDPVAService notifyDPVAService;

    @ClientCacheEntryCreated
    public void entryCreated(ClientCacheEntryCreatedEvent<String> event) {
        updateData(event.getKey());
    }
    @ClientCacheEntryModified
    public void entryModified(ClientCacheEntryModifiedEvent<String> event) {
        updateData(event.getKey());
    }

    public void updateData(String wellUid) {
        notifyDPVAService.notifyDPVAJobForSurveyData(wellUid, "active");
    }
}
