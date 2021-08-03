package com.moblize.ms.dailyops.service.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoleSectionDTO {
    public enum HoleSectionType {
        ALL("ALL"),
        SURFACE("SURFACE"),
        INTERMEDIATE("INTERMEDIATE"),
        CURVE("CURVE"),
        LATERAL("LATERAL"),
        UNKNOWN("UNKNOWN");

        private String holeSectionType;

        HoleSectionType(String type) {
            this.holeSectionType = type;
        }

        public String type() {
            return holeSectionType;
        }
    }

    private HoleSectionType section = HoleSectionType.ALL;

    private Float fromDepth;

    private Float toDepth;

    private Long wellboreId;

    private Float odSize;

    private Float weight;
}
