package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.mongo.WellPerformanceMetaData;

public interface WellPerformanceMetaDataService {
    WellPerformanceMetaData save(WellPerformanceMetaData wellPerformanceMetaData);

    WellPerformanceMetaData update(WellPerformanceMetaData updatedData);

    WellPerformanceMetaData getByWellUid(String wellUid);

    void deleteWellPerformanceMetaData(String wellUid);
}
