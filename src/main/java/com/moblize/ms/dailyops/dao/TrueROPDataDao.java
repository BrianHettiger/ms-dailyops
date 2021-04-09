package com.moblize.ms.dailyops.dao;

import com.moblize.ms.dailyops.repository.TrueROPDataRepository;
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
public class TrueROPDataDao {

    @Autowired
    TrueROPDataRepository trueROPDataRepository;

    public Long getLastProcessUpTo(String wellUid) {
        return trueROPDataRepository.maxTime(wellUid);
    }


}
