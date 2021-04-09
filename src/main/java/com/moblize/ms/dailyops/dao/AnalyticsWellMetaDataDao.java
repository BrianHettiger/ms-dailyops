package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.AnalyticsWellMetaData;
import com.moblize.ms.dailyops.repository.AnalyticsWellMetaDataRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
@Component
public class AnalyticsWellMetaDataDao {

    @Autowired
    AnalyticsWellMetaDataRepository analyticsWellMetaDataRepository;

    public AnalyticsWellMetaData getLastProcessUpTo(String wellUid) {
        return analyticsWellMetaDataRepository.findFirstByWellUid(wellUid);
    }


}
