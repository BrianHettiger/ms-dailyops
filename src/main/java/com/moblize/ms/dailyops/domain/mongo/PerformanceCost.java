package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "performanceCost")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class PerformanceCost {

    @Id
    private String id;
    public String uid;
    public Cost cost;
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class Cost implements Serializable {
        public Double afe = null;
        public Double perFt = null;
        public Double perLatFt = null;
        public Double total = null;
    }
}
