
package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "section"
})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class AvgROP {

    @JsonProperty("section")
    public Section section;

}
