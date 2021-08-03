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
public class Location implements Serializable {

    @ProtoField(number = 1)
    Double lng = 0.0;

    @ProtoField(number = 2)
    Double lat = 0.0;

}
