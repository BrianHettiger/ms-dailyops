package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moblize.ms.dailyops.service.dto.ScaledTargetWindow;
import com.moblize.ms.dailyops.service.dto.TargetWindowPerFootDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "targetWindowPerFootDPVA")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class TargetWindowPerFootDPVA {
    @Id
    String id;
    private String wellUid;
    private String customer;
    private String wellStatus;
    @Embedded
    private List<ScaledTargetWindow> basic = new ArrayList<>();
    @Embedded
    private List<ScaledTargetWindow> advance = new ArrayList<>();

    private List<List<Float>> pvFirstLine = new ArrayList<>();
    private List<List<Float>> pvCenterLine = new ArrayList<>();
    private List<List<Float>> pvLastLine = new ArrayList<>();
    private List<List<List<Float>>> pvSideLine = new ArrayList<>();

    private List<List<Float>> svFirstLine = new ArrayList<>();
    private List<List<Float>> svCenterLine = new ArrayList<>();
    private List<List<Float>> svLastLine = new ArrayList<>();
    private List<List<List<Float>>> svSideLine = new ArrayList<>();

    @Embedded
    private List<Intersection> svIntersections = new ArrayList<>();
    @Embedded
    private List<Intersection> pvIntersections = new ArrayList<>();

    @CreatedDate
    LocalDateTime addedAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
}
