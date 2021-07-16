
package com.moblize.ms.dailyops.domain.mongo;

import java.util.List;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "targetWindowDPVA")
@JsonIgnoreProperties(value = {"id", "addedAt", "updatedAt"})
public class TargetWindowDPVA {

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


    @Getter
    @Setter
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
    public static  class Basic {
        public SectionView sectionView;
        public PlanView planView;
    }
    @Getter
    @Setter
    public static  class PlanView {
        public Integer left;
        public Integer right;
    }
    @Getter
    @Setter
    public static  class SectionView {
        public Integer above;
        public Integer below;
    }

}
