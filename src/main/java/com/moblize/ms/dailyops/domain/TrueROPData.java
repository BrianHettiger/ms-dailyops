package com.moblize.ms.dailyops.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Table(name = "true_rop")
@Entity
@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor
public class TrueROPData {
    @Id
    private Long id;
    @Column(name = "well_uid", nullable = false)
    private String wellUid;

    @Column(name = "time", nullable = false)
    private Long time;

    @Column(name = "start_hole_depth", nullable = false)
    private Float startHoleDepth;

    @Column(name = "end_hole_depth", nullable = false)
    private Float endHoleDepth;

    @Column(name = "start_bit_depth", nullable = false)
    private Float startBitDepth;

    @Column(name = "end_bit_depth", nullable = false)
    private Float endBitDepth;

    @Column(name = "footage_drilled_in_total", nullable = false)
    private Float footageDrilledInTotal;

    @Column(name = "rotary_footage", nullable = false)
    private Float rotaryFootage;

    @Column(name = "slide_footage", nullable = false)
    private Float slideFootage;

    @Column(name = "rotary_drilling_time", nullable = false)
    private Integer rotaryDrillingTime;

    @Column(name = "slide_drilling_time", nullable = false)
    private Integer slideDrillingTime;

    @Column(name = "on_bottom_time", nullable = false)
    private Integer onBottomTime;

    @Column(name = "hole_section", nullable = false)
    private String section;

    @Column(name = "is_tripping", nullable = false)
    private Boolean isTripping;

    @Column(name = "tripping_time", nullable = false)
    private Integer trippingTime;

    @Column(name = "is_data_fill", nullable = false)
    private Boolean isDataFill;

    @Column(name = "added_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss aa")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt = new Date();

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM d, yyyy h:mm:ss aa")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}
