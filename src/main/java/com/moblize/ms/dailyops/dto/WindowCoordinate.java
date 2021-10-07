package com.moblize.ms.dailyops.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WindowCoordinate implements Serializable {
    @ProtoField(number = 1)
    Float xAxis = 0.0f;

    @ProtoField(number = 2)
    Float yAxis = 0.0f;
}
