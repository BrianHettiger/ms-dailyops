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
    String tiINC = "";
    @ProtoField(number = 3)
    String tiAZI = "";
    @ProtoField(number = 4)
    String ti3D = "";
}
