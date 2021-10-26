package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "bcwDepthLog")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class BCWSmoothLogData {

    @Id
    private String id;
    private String uid;
    private String bcwId;
    private List<DepthLogResponse> depthLogResponseList;

    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
