package com.moblize.ms.dailyops.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "silent",
    "data"
})
public class LogResponse {

    @JsonProperty("silent")
    public Boolean silent;
    @JsonProperty("data")
    public List<DepthLogReponse> data = new ArrayList<>();

}
