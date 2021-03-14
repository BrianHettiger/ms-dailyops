package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.mongo.PerformanceBHA;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceBHARepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
@Component
public class PerformanceBHADao {


    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PerformanceBHARepository performanceBHARepository;

    public PerformanceBHA savePerformanceBHA(PerformanceBHA performanceBHADTO){
        PerformanceBHA dbObj =  performanceBHARepository.findFirstByUid(performanceBHADTO.getUid());
        if (null != dbObj) {
            return updatePerformanceBHA(performanceBHADTO, dbObj);
        } else {
            return performanceBHARepository.save(performanceBHADTO);
        }
    }

    public PerformanceBHA updatePerformanceBHA(PerformanceBHA performanceBHADTO){
        return updatePerformanceBHA(performanceBHADTO, null);
    }

    public PerformanceBHA updatePerformanceBHA(PerformanceBHA performanceBHADTO, PerformanceBHA existingObj){
        PerformanceBHA dbObj;
        if (null == existingObj) {
            dbObj = performanceBHARepository.findFirstByUid(performanceBHADTO.getUid());
        } else {
            dbObj = existingObj;
        }

        if(null != performanceBHADTO.getBha() && !performanceBHADTO.getBha().isEmpty() ) {
            dbObj.getBha().clear();
            dbObj.setBha(performanceBHADTO.getBha());
            return performanceBHARepository.save(dbObj);
        }
        return existingObj;
    }

    public PerformanceBHA findPerformanceBHA(String uid) {
        return performanceBHARepository.findFirstByUid(uid);
    }

    public void deletePerformanceBHA(String uid){
        performanceBHARepository.deleteByUid(uid);
    }
}
