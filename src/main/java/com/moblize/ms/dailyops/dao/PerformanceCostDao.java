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
        PerformanceCost dbObj =  performanceCostRepository.findFirstByUid(performanceCostDTO.getUid());
        if (null != dbObj) {
            return updatePerformanceCost(performanceCostDTO, dbObj);
        } else {
            return performanceCostRepository.save(performanceCostDTO);
        }
    }

    public PerformanceCost updatePerformanceCost(PerformanceCost performanceCostDTO){
        return updatePerformanceCost(performanceCostDTO, null);
    }

    public PerformanceCost updatePerformanceCost(PerformanceCost performanceCostDTO, PerformanceCost existingObj){
        PerformanceCost dbObj;
        if (null == existingObj) {
            dbObj = performanceCostRepository.findFirstByUid(performanceCostDTO.getUid());
        } else {
            dbObj = existingObj;
        }
        if(null != performanceCostDTO.getCost()) {
            if (null == dbObj.getCost()) {
                dbObj.setCost(new PerformanceCost.Cost());
            }
            if (null != performanceCostDTO.getCost().getAfe()) {
                performanceCostDTO.getCost().setAfe(performanceCostDTO.getCost().getAfe());
            }
            if (null != performanceCostDTO.getCost().getPerFt()) {
                performanceCostDTO.getCost().setPerFt(performanceCostDTO.getCost().getPerFt());
            }
            if (null != performanceCostDTO.getCost().getPerLatFt()) {
                performanceCostDTO.getCost().setPerLatFt(performanceCostDTO.getCost().getPerLatFt());
            }
            if (null != performanceCostDTO.getCost().getTotal()) {
                performanceCostDTO.getCost().setTotal(performanceCostDTO.getCost().getTotal());
            }
        }
        return performanceCostRepository.save(dbObj);
    }

    public PerformanceCost findPerformanceCost(String uid) {
        return performanceCostRepository.findFirstByUid(uid);
    }

    public void deletePerformanceCost(String uid){
        performanceCostRepository.deleteByUid(uid);
    }
}
