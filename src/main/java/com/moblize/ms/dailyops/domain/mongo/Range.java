package com.moblize.ms.dailyops.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Range {
    private Float start = 0f;
    private Float end = 0f;
    private Float diff = 0f;
    private Float tvdStart = 0f;
    private Float tvdEnd = 0f;
}
