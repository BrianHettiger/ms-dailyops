package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.domain.Wellbore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WellboreRepository extends JpaRepository<Wellbore, Long>, JpaSpecificationExecutor<Wellbore> {

    Wellbore findFirstByWellUid(String wellUid);

}
