package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.domain.TrueROPData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrueROPDataRepository extends JpaRepository<TrueROPData, Long>, JpaSpecificationExecutor<TrueROPData> {

    TrueROPData findFirstByWellUid(String wellUid);

    @Query(value = "SELECT max(time) FROM TrueROPData where wellUid = :wellUid")
    public Long maxTime(@Param("wellUid") String wellUid);
}
