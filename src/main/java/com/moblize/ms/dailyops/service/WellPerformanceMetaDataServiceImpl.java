package com.moblize.ms.dailyops.service;


import com.moblize.ms.dailyops.domain.mongo.WellPerformanceMetaData;
import com.moblize.ms.dailyops.repository.mongo.client.WellPerformanceMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
public class WellPerformanceMetaDataServiceImpl implements WellPerformanceMetaDataService {

    @Autowired
    private WellPerformanceMetaDataRepository metaDataRepository;

    @Override
    public WellPerformanceMetaData save(final WellPerformanceMetaData wellPerformanceMetaData) {
        wellPerformanceMetaData.setAddedAt(LocalDateTime.now());
        wellPerformanceMetaData.setUpdatedAt(LocalDateTime.now());
        return metaDataRepository.save(wellPerformanceMetaData);
    }

    @Override
    public WellPerformanceMetaData update(final WellPerformanceMetaData updatedData) {
        if(null == updatedData.getId() || updatedData.getId().isEmpty()) {
            final WellPerformanceMetaData oldData = metaDataRepository.findFirstByWellUid(updatedData.getWellUid());
            updatedData.setId(oldData.getId());
        }
        if(null != updatedData.getAddedAt()){
            updatedData.setAddedAt(null);
        }
        updatedData.setUpdatedAt(LocalDateTime.now());
        return metaDataRepository.save(updatedData);
    }

    @Override
    public WellPerformanceMetaData getByWellUid(final String wellUid) {
        return metaDataRepository.findFirstByWellUid(wellUid);
    }

    @Override
    public void deleteWellPerformanceMetaData(final String wellUid) {
       Object response = metaDataRepository.deleteByWellUid(wellUid);
    }
}
