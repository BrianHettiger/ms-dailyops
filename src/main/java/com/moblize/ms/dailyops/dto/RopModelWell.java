package com.moblize.ms.dailyops.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor
public class RopModelWell implements Serializable
{
    private long userProfileId;
    private String welluid;
    private String wellName;
    private String wellboreuid;
    private String wellboreName;
    private String ropWelluid;
    private String ropWellName;
    private String ropWellboreuid;
    private String ropWellboreName;
    private String ropModelId;
    private double ropMaxVal;
    private String r0;
    private String r1;
    private String r2;
    private String r3;
    private String r4;
    private String r5;
    private String r6;
    private String r7;
    private String r8;
    private String r9;

}
