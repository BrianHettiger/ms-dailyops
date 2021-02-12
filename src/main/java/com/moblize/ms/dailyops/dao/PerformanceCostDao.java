package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.mongo.PerformanceCost;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceCostRepository;
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
public class PerformanceCostDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PerformanceCostRepository performanceCostRepository;


    public PerformanceCost savePerformanceCost(PerformanceCost performanceCostDTO){
        PerformanceCost dbObj =  performanceCostRepository.findByUid(performanceCostDTO.getUid());
        if (null != dbObj) {
            performanceCostDTO.setId(dbObj.getId());
        }
       return performanceCostRepository.save(performanceCostDTO);
    }

    public PerformanceCost updatePerformanceCost(PerformanceCost performanceCostDTO){
        PerformanceCost dbObj =  performanceCostRepository.findByUid(performanceCostDTO.getUid());
        performanceCostDTO.setId(dbObj.getId());
        return performanceCostRepository.save(performanceCostDTO);
    }

    public PerformanceCost findPerformanceCost(String uid) {
        return performanceCostRepository.findByUid(uid);
    }

    public void deletePerformanceCost(String uid){
        performanceCostRepository.deleteByUid(uid);
    }


}
