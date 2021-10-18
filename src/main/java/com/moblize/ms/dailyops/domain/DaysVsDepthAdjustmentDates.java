package com.moblize.ms.dailyops.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@NoArgsConstructor
public class DaysVsDepthAdjustmentDates {
    @ProtoField(number = 1)
    Float spudDate;
    @ProtoField(number = 2)
    Float suspendDate;
    @ProtoField(number = 3)
    Float resumeDate;
    @ProtoField(number = 4)
    Float totalDepthDate;
    @ProtoField(number = 5)
    Float releaseDate;
}
