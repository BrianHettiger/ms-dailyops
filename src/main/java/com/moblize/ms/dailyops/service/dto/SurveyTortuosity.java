package com.moblize.ms.dailyops.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyTortuosity {
    @ProtoField(number = 1)
    Double md;
    @ProtoField(number = 2)
    Double tiINC = 0d;
    @ProtoField(number = 3)
    Double tiAZI = 0d;
    @ProtoField(number = 4)
    Double ti3D = 0d;
}
