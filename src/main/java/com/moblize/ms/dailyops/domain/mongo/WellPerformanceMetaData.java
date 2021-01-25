package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WellPerformanceMetaData implements Serializable {
    @Id
    @JsonIgnore
    @Column(name = "_id")
    private ObjectId id;
    private String wellUid;
    private Double processedUntilDepth;
    @JsonProperty("rop")
    private RopMetaData ropMetaData;
    @JsonProperty("cost")
    private CostMetaData costMetaData;
    //@CreatedDate
    private LocalDateTime addedAt;
    //@LastModifiedDate
    private LocalDateTime updatedAt;
}
