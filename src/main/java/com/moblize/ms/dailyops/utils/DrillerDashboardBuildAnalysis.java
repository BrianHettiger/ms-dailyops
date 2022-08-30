package com.moblize.ms.dailyops.utils;

import com.moblize.ms.dailyops.domain.LogChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pengfeig
 */


public class DrillerDashboardBuildAnalysis{
    public static final List<String> CHANNELS = new ArrayList<>();

    static {
        CHANNELS.add(LogChannel.RIG_STATE_CHANNEL_NAME);
        CHANNELS.add(LogChannel.ROP_CHANNEL_NAME);
        CHANNELS.add(LogChannel.WEIGHT_ON_BIT_MAX_CHANNEL_NAME);
        CHANNELS.add(LogChannel.DIFF_PRESSURE_CHANNEL_NAME);
        CHANNELS.add(LogChannel.SURFACE_TORQUE_MAX_CHANNEL_NAME);
        CHANNELS.add(LogChannel.RPMA_CHANNEL_NAME);
        CHANNELS.add(LogChannel.MUD_FLOW_IN_CHANNEL_NAME);
    }
}
