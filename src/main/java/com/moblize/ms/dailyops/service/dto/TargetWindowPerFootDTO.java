package com.moblize.ms.dailyops.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TargetWindowPerFootDTO {
    @ProtoField(number = 1)
    String wellUid;
    @ProtoField(number = 2)
    String customer;
    @ProtoField(number = 3)
    String wellStatus;
    @ProtoField(number = 4, collectionImplementation = ArrayList.class)
    List<ScaledTargetWindow> basic = new ArrayList<>();
    @ProtoField(number = 5, collectionImplementation = ArrayList.class)
    List<ScaledTargetWindow> advance = new ArrayList<>();
}
