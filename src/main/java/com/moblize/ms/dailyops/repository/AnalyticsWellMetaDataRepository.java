package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.domain.AnalyticsWellMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsWellMetaDataRepository extends JpaRepository<AnalyticsWellMetaData, Long>, JpaSpecificationExecutor<AnalyticsWellMetaData> {

    AnalyticsWellMetaData findFirstByWellUid(String wellUid);
}
