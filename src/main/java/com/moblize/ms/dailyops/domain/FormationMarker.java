package com.moblize.ms.dailyops.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@PersistenceUnit(name="default")
@Table(name="formation_marker")
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class FormationMarker {

    @Id
    @GeneratedValue
    @JsonProperty(value = "id")
    private Long id;

	@Column(name="uidWell")
    @JsonProperty(value = "wellUid")
	private String wellUid;

	@Column(name="uidwellbore")
    @JsonProperty(value = "wellboreUid")
	private String wellboreUid;

	@Column(name="name")
    @JsonProperty(value = "name")
	private String name;

	@Column(name="tvd")
    @JsonProperty(value = "TVD")
	private Float TVD;

	@Column(name="md")
    @JsonProperty(value = "MD")
	private Float MD;

	@Column(name="targetformation")
    @JsonProperty(value = "targetFormation")
	private boolean targetFormation;



}
