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
        private Double all = null;
        private Double surface = null;
        private Double intermediate = null;
        private Double curve = null;
        private Double lateral = null;
    }
}
