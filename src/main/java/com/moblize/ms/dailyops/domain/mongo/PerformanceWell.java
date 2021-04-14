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
    private BySection totalDays = new BySection();
    private BySection footagePerDay = new BySection();
    private BySection slidingPercentage = new BySection();
    private Map<String, Range> holeSectionRange = new HashMap<>();
    private BySection avgDLSBySection = new BySection();
    private BySection avgMYBySection = new BySection();
    private SectionDirection avgDirection = new SectionDirection();
    private BySection avgDirectionAngle = new BySection();
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class SectionDirection {
        private Section section = new Section();
    }
    @Getter
    @Setter
    public static class Section  {
        private String all = null;
        private String surface = null;
        private String intermediate = null;
        private String curve = null;
        private String lateral = null;
    }
}
