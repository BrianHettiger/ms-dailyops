package com.moblize.ms.dailyops.domain;

import com.moblize.ms.dailyops.service.dto.HoleSection;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PersistenceUnit(name = "default")
@Table(name = "wb_wellbore")
public class Wellbore
{
    @Id
    @Column(name="wb_wellbore_pkey")
    private Long id;

  //  @Unique
    @Column(name="uid")
    private String uid;

  //  @Required
    @Column(name="wb_name")
    private String name;

    @Column(name="uidwell")
    private String wellUid;

    @Column(name="wb_namewell")
    private String wellName;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "wellboreid", referencedColumnName = "wb_wellbore_pkey" )
    private List<HoleSection> holeSectionList;


}
