package com.moblize.ms.dailyops.dto;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "primaryWellUid",
    "offsetWellUidList"
})
public class TortuosityRequestDTO {

    @JsonProperty("primaryWellUid")
    public String primaryWellUid;
    @JsonProperty("offsetWellUidList")
    public List<String> offsetWellUidList = null;

}
