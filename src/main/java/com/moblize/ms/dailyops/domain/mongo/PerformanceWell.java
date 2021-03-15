package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "performanceWell")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class PerformanceWell {
    @Id
    private String id;
    private String uid;
    private BySection footagePerDay = new BySection();
    private BySection slidingPercentage = new BySection();
    private Map<String, Range> holeSectionRange = new HashMap<>();
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
