package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.service.dto.HoleSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface HoleSectionRepository extends JpaRepository<HoleSection, Long>, JpaSpecificationExecutor<HoleSection> {
    List<HoleSection> findAllByWellboreIdIn(Collection<Long> wellBoreId);
    List<HoleSection> findByWellboreId(Long wellboreId);

}
