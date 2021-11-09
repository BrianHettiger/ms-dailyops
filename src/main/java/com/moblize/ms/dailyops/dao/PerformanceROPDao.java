package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.PerformanceROP;
import com.moblize.ms.dailyops.repository.mongo.client.PerformanceROPRepository;
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
public class PerformanceROPDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PerformanceROPRepository performanceROPRepository;


    public PerformanceROP savePerformanceROP(PerformanceROP performanceROPDTO){
        PerformanceROP dbObj =  performanceROPRepository.findByUid(performanceROPDTO.getUid());
        if (null != dbObj) {
            performanceROPDTO.set_id(dbObj.get_id());
        }
        return performanceROPRepository.save(performanceROPDTO);
    }
    public PerformanceROP findPerformanceROP(String uid) {
        return performanceROPRepository.findByUid(uid);
    }

    public void deletePerformanceROP(String uid){
        performanceROPRepository.deleteByUid(uid);
    }


}
