package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.domain.DrillingProfileData;
import com.moblize.ms.dailyops.domain.TrueROPData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillingProfileDataRepository extends JpaRepository<DrillingProfileData,Long>, JpaSpecificationExecutor<DrillingProfileData>{
    List<DrillingProfileData> findByWellUid(String wellUid);

}
