package com.moblize.ms.dailyops.service.dto;


import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

/**
 * A HoleSection.
 */
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class HoleSection {

    public enum HoleSectionType {
        ALL("ALL"),
        SURFACE("SURFACE"),
        INTERMEDIATE("INTERMEDIATE"),
        CURVE("CURVE"),
        LATERAL("LATERAL"),
        UNKNOWN("UNKNOWN");

        private final String holeSectionType;

        HoleSectionType(String type) {
            this.holeSectionType = type;
        }

        public String type() {
            return holeSectionType;
        }
    }
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private HoleSectionType section = HoleSectionType.ALL;

    private Float fromDepth;

    private Float toDepth;

    private Long wellboreId;

    private Float odSize;

    private Float weight;

    private Float idSize;
}
