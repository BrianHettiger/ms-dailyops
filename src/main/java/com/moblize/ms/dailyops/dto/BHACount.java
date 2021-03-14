package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class BHACount {

    @JsonProperty("sec")
    public Section section;

    @Getter
    @Setter
    public static class Section implements Serializable {

        @JsonProperty("a")
        public int all;
        @JsonProperty("s")
        public int surface;
        @JsonProperty("i")
        public int intermediate;
        @JsonProperty("c")
        public int curve;
        @JsonProperty("l")
        public int lateral;
    }
}
