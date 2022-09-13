package com.moblize.ms.dailyops.repository;

import com.moblize.ms.dailyops.domain.FormationMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationMarkerRepository extends JpaRepository<FormationMarker, Long>, JpaSpecificationExecutor<FormationMarker> {
    List<FormationMarker> findByWellUidInAndWellboreUidOrderByTVDAsc(List<String> wellUid, String wellboreUid);
    List<FormationMarker> findByWellUidAndWellboreUidOrderByTVDAsc(String wellUId, String name);
    FormationMarker findFirstById(Long id);
}
