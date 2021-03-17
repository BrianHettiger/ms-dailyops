
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    public Double afe;
    @JsonProperty("perFt")
    public Double perFt;
    @JsonProperty("perLatFt")
    public Double perLatFt;
    @JsonProperty("total")
    public Double total;

}
