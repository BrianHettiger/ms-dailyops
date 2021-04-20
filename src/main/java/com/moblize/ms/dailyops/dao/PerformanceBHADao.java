package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceBHARepository;
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
public class PerformanceBHADao {

    @Autowired
    private PerformanceBHARepository performanceBHARepository;

    public PerformanceBHA saveUpdatePerformanceBHA(PerformanceBHA performanceBHADTO){
        PerformanceBHA dbObj =  performanceBHARepository.findFirstByUid(performanceBHADTO.getUid());
        if (null != dbObj) {
            performanceBHADTO.setId(dbObj.getId());
            performanceBHADTO.setAddedAt(dbObj.getAddedAt());
        }
            return performanceBHARepository.save(performanceBHADTO);
    }


    public PerformanceBHA findPerformanceBHA(String uid) {
        return performanceBHARepository.findFirstByUid(uid);
    }

    public void deletePerformanceBHA(String uid){
        performanceBHARepository.deleteByUid(uid);
    }
}
