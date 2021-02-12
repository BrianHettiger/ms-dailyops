package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "wellPerformanceMetaData")
public class WellPerformanceMetaData implements Serializable {
    @Id
    private String id;

    private String wellUid;
    private Double processedUntilDepth;

    @JsonIgnore
    private LocalDateTime addedAt;

    @JsonIgnore
    private LocalDateTime updatedAt;
}
