package com.moblize.ms.dailyops.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "analytics_well_meta_data")
public class AnalyticsWellMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "welluid")
    private String wellUid;
    @Column(name = "lastreset")
    private Long lastReset;
    @Column(name = "lastprocessupto")
    private Long lastProcessUpTo = 0L;
    @Column(name = "lastfinishedat")
    private Long LastFinishedAt;
    @Column(name = "lasttrippingandcasingchecked")
    private Long lastTrippingAndCasingChecked;
    @Column(name = "lastrunattemptstart")
    private Long lastRunAttemptStart;
    @Column(name = "comment")
    private String comment;
    @Column(name = "lastruntimestampofascenteveryconntadmodel")
    private Long lastRunTimeStampOfAscentEveryConnTadModel;


}
