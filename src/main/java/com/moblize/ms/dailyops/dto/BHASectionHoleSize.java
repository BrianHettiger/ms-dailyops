package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BHASectionHoleSize implements Serializable {

    @ProtoField(number = 1)
    @JsonProperty("a")
    Set<Float> all = new HashSet<>();
    @ProtoField(number = 2)
    @JsonProperty("s")
    Set<Float> surface = new HashSet<>();
    @ProtoField(number = 3)
    @JsonProperty("i")
    Set<Float> intermediate = new HashSet<>();
    @ProtoField(number = 4)
    @JsonProperty("c")
    Set<Float> curve = new HashSet<>();
    @ProtoField(number = 5)
    @JsonProperty("l")
    Set<Float> lateral = new HashSet<>();
}
