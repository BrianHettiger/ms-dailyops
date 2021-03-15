package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.mongo.PerformanceWell;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceWellRepository;
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
public class PerformanceWellDao {

    @Autowired
    private PerformanceWellRepository performanceWellRepository;

    public PerformanceWell saveUpdatePerformanceWell(PerformanceWell performanceWellDTO){
        PerformanceWell dbObj =  performanceWellRepository.findFirstByUid(performanceWellDTO.getUid());
        if (null != dbObj) {
            performanceWellDTO.setId(dbObj.getId());
            performanceWellDTO.setAddedAt(dbObj.getAddedAt());
        }
            return performanceWellRepository.save(performanceWellDTO);
    }


    public PerformanceWell findPerformanceWell(String uid) {
        return performanceWellRepository.findFirstByUid(uid);
    }

    public void deletePerformanceWell(String uid){
        performanceWellRepository.deleteByUid(uid);
    }
}
