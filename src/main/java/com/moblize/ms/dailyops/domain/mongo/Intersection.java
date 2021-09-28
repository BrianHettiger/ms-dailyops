package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

@Getter @Setter @NoArgsConstructor
public class Intersection {
    @JsonAlias("xAxis")
    @ProtoField(number = 1)
    Double value;

    @JsonProperty("isIn")
    @ProtoField(number = 2)
    Boolean isIn;

//    public Intersection(final Double xAxis, final boolean isIn) {
//        this.xAxis = xAxis;
//        this.isIn = isIn;
//    }
}
