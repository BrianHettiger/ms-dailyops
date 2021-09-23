package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter
@Setter
public class BHASectionCount implements Serializable {

    @ProtoField(number = 1)
    @JsonProperty("a")
    Integer all = 0;
    @ProtoField(number = 2)
    @JsonProperty("s")
    Integer surface = 0;
    @ProtoField(number = 3)
    @JsonProperty("i")
    Integer intermediate = 0;
    @ProtoField(number = 4)
    @JsonProperty("c")
    Integer curve = 0;
    @ProtoField(number = 5)
    @JsonProperty("l")
    public Integer lateral = 0;
}
