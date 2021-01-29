package com.moblize.ms.dailyops.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document
public class NearByWellRequestDTO {
    @NotNull
    private String primaryWell;
    @NotNull
    private int distance;
    @NotNull
    private int limit;
    @NotNull
    private String customer;

    private List<String> offsetWells;
}
