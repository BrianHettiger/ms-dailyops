package com.moblize.ms.dailyops.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.moblize.ms.dailyops.domain.DaysVsDepthAdjustmentDates;
import com.moblize.ms.dailyops.domain.Rig;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uid",
    "name",
    "country",
    "state",
    "statusWell",
    "fracStatusWell",
    "customer",
    "county",
    "wellViewUid",
    "pad",
    "isBCW",
    "isHidden",
    "isMetricSystem",
    "regionId",
    "createdAt",
    "completedAt",
    "timeZone",
    "rigs",
    "location",
    "daysVsDepthAdjustmentDates",
    "dist",
    "convertChannels"
})
@Data
public class OffsetWell {
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("country")
    private String country;
    @JsonProperty("state")
    private String state;
    @JsonProperty("statusWell")
    private String statusWell;
    @JsonProperty("fracStatusWell")
    private String fracStatusWell;
    @JsonProperty("customer")
    private String customer;
    @JsonProperty("county")
    private String county;
    @JsonProperty("wellViewUid")
    private String wellViewUid;
    @JsonProperty("pad")
    private String pad;
    @JsonProperty("isBCW")
    private Boolean isBCW;
    @JsonProperty("isHidden")
    private Boolean isHidden;
    @JsonProperty("isMetricSystem")
    private Boolean isMetricSystem;
    @JsonProperty("regionId")
    private String regionId;
    @JsonProperty("createdAt")
    private Long createdAt;
    @JsonProperty("completedAt")
    private Long completedAt;
    @JsonProperty("timeZone")
    private String timeZone;
    @JsonProperty("rigs")
    private List<Rig> rigs = null;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("daysVsDepthAdjustmentDates")
    private DaysVsDepthAdjustmentDates daysVsDepthAdjustmentDates;
    @JsonProperty("dist")
    private Dist dist;
    @JsonProperty("convertChannels")
    private List<ConvertChannel> convertChannels = null;
    public boolean selected;
    public Double ropPerceivedall;
}
