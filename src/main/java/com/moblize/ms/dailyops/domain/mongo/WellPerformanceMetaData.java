package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.moblize.ms.dailyops.dto.DayVsDepth;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "wellPerformanceMetaData")
public class WellPerformanceMetaData implements Serializable {
    @Id
    private String id;

    private String wellUid;
    private Double processedUntilDepth;
    private Long ropCalculatedUntilTime = 0L;
    private Long ropBHACalculatedUntilTime = 0L;
    private Map<String, Number> ropMetaData = new HashMap<>();
    private Map<String, List<DayVsDepth>> dvdMetaBySection = new HashMap<>();
    private Long dayVsDepthFetchedUntilTime = 0L;

    @JsonIgnore
    private LocalDateTime addedAt;

    @JsonIgnore
    private LocalDateTime updatedAt;
}
