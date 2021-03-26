
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonPropertyOrder({
    "afe",
    "perFt",
    "perLatFt",
    "total"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Cost {

    @JsonProperty("afe")
    public Double afe = null;
    @JsonProperty("perFt")
    public Double perFt  = null;
    @JsonProperty("perLatFt")
    public Double perLatFt  = null;
    @JsonProperty("total")
    public Double total = null;

}
