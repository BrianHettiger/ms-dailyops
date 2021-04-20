package com.moblize.ms.dailyops.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DayVsDepth implements Serializable {
    private Long datetime = 0L;
    private Double depth = 0D;
    private Double avgROP = 0D;
    private Double days = 0D;
}
