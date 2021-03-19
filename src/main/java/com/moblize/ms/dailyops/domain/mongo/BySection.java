package com.moblize.ms.dailyops.domain.mongo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class BySection {
    private Section section = new Section();
    @Getter
    @Setter
    public static class Section implements Serializable {
        private Double all = 0D;
        private Double surface = 0D;
        private Double intermediate = 0D;
        private Double curve = 0D;
        private Double lateral = 0D;
    }
}
