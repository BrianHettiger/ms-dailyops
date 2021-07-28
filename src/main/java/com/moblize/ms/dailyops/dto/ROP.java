package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ROP implements Serializable {
    @ProtoField(number = 1)
    @JsonProperty("sec")
    public Section section = new Section();
}
