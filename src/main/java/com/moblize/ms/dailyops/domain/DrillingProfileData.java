package com.moblize.ms.dailyops.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "drilling_profile_data")
@PersistenceUnit(name = "default")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrillingProfileData {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

	@Column(name = "well_uid", nullable = false)
	private String wellUid;

	@Column(name = "hole_depth", nullable = false)
	private Float holeDepth;

	@Column(name = "cost", nullable = false)
    private Float cost;

	@Column(name = "mud_weight", nullable = false)
    private Float mudWeight;

	@Column(name = "days", nullable = false)
    private Float days;
}
