
package com.moblize.ms.dailyops.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "targetWindowDPVA")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class TargetWindowDPVA {

    @Id
    private String id;
    @JsonProperty("uid")
    public String uid;
    @JsonProperty("isEnable")
    public Boolean isEnable;
    @JsonProperty("selectedMode")
    public String selectedMode;
    @JsonProperty("basic")
    public Basic basic;
    @JsonProperty("advance")
    public List<Advance> advance = null;
    @CreatedDate
    private LocalDateTime addedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Advance {
        public Double targetVS;
        public Double tvdTop;
        public Double tvdBottom;
        public Double windowTop;
        public Double windowBottom;
        public Double windowLeft;
        public Double windowRight;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Basic {
        public SectionView sectionView;
        public PlanView planView;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class PlanView {
        public Integer leftAndRight;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class SectionView {
        public Integer aboveAndBelow;
    }

}
