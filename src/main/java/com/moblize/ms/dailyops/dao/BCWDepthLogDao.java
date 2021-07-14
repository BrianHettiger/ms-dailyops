package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.domain.mongo.BCWDepthLog;
import com.moblize.ms.dailyops.repository.mongo.client.BCWDepthLogRepository;
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
public class BCWDepthLogDao {

    @Autowired
    private BCWDepthLogRepository bcwDepthLogRepository;

    public BCWDepthLog saveUpdateBCWDepthLog(final BCWDepthLog bcwDepthLog){
        return bcwDepthLogRepository.save(bcwDepthLog);
    }

}
