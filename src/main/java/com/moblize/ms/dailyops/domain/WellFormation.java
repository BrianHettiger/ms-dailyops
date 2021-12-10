package com.moblize.ms.dailyops.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@PersistenceUnit(name = "default")
@Table(name = "well_formation")
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class WellFormation {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name="well_uid", nullable = false)
    private String wellUID;
    @Column(name="formation_type", nullable = false)
    private String formationName;
    @Column(name="highest_rop_avg", nullable = false)
    private float highestRopAvg;
    @Column(name="start_depth", nullable = false)
    private float startDepth;
    @Column(name="diff_pressure_avg", nullable = false)
    private float diffPressureAvg;
    @Column(name="mud_flow_avg", nullable = false)
    private float mudFlowAvg;
    @Column(name="pump_pressure_avg", nullable = false)
    private float pumpPressureAvg;
    @Column(name="surface_torque_max", nullable = false)
    private float surfaceTorqueMax;
    @Column(name="weight_on_bit_max", nullable = false)
    private float weightOnBitMax;
    @Column(name="rpma_avg", nullable = false)
    private float rpmaAvg;
    @Column(name="hole_size", nullable = false)
    private float holeSize;

    @Column(name = "added_at", insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date addedAt = new java.util.Date();

    @Column(name = "updated_at", insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt = new Date();



}
