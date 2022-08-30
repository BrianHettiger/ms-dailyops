package com.moblize.ms.dailyops.utils.uom;


import com.moblize.core.model.uom.UOMAttributeMetadata;
import com.moblize.ms.dailyops.domain.mongo.MongoLog;

import java.util.ArrayList;
import java.util.List;


public class UOM {

    public static final UOMAttributeMetadata[] TRUE_ROP_DATA_ATTRIBUTES = {
            attribute("ROPAvg", "startHoleDepth"),
            attribute("ROPAvg", "endHoleDepth"),
            attribute("ROPAvg", "startBitDepth"),
            attribute("ROPAvg", "endBitDepth"),
            attribute("ROPAvg", "rotaryFootage"),
            attribute("ROPAvg", "slideFootage"),
            attribute("ROPAvg", "footageDrilledInTotal")
    };

    public static final UOMAttributeMetadata[] FORMATION_MARKER_ATTRIBUTES = {
            attribute("Depth", "TVD"),
            attribute("Depth", "MD")
    };

    public static final UOMAttributeMetadata[] SLIP_CONNECTION_ATTRIBUTES = {
            attribute("HoleDepth", "holeDepth"),
            attribute("BitDepth", "bitDepth")
    };

    public static final UOMAttributeMetadata[] B2B_CONNECTION_ATTRIBUTES = {
            attribute("HoleDepth", "holeDepth")
    };

    public static final UOMAttributeMetadata[] TRIPPING_CASING_RECORD_ATTRIBUTES = {
            attribute("HoleDepth", "holeDepth"),
            attribute("BitDepth", "startBitDepth"),
            attribute("BitDepth", "endBitDepth")
    };


    public static final UOMAttributeMetadata[] DAYS_VS_DEPTH_ATTRIBUTES = {
            attribute("ROPAvg", "avgROP"),
            attribute("HoleDepth", "depth"),
    };

    public static final UOMAttributeMetadata[] ON_BOTTOM_HOUR_DATA_ATTRIBUTES = {
            attribute("HoleDepth", "holeDepth"),
    };

    public static final UOMAttributeMetadata[] BHA_ATTRIBUTES = {
            attribute("HoleSize", "holeSize"),
            attribute("Depth", "depthOut"),
            attribute("Depth", "depthIn")
    };

    public static final UOMAttributeMetadata[] HOLE_SECTION_ATTRIBUTES = {
            attribute("Depth", "fromDepth"),
            attribute("Depth", "toDepth")
    };

    public static final UOMAttributeMetadata[] TRAJECTORY_ATTRIBUTES = {
            attribute("SurveyMD", "stations.md"),
            attribute("SurveyTVD", "stations.tvd"),
            attribute("SurveyDLS", "stations.dls"),
            attribute("VertSection", "stations.vertSect"),
            attribute("CartesianCoordinates", "stations.ns"),
            attribute("CartesianCoordinates", "stations.ew"),
    };

    public static final UOMAttributeMetadata[] WELL_PLAN_ATTRIBUTES = {
            attribute("MD", "measuredDepth"),
            attribute("VertSection", "trueVerticalDepth"),
            attribute("VertSection", "verticalSection"),
            attribute("CartesianCoordinates", "northSouth"),
            attribute("CartesianCoordinates", "eastWest"),
            attribute("SurveyDLS", "dogLeg"),
    };

    public static final UOMAttributeMetadata[] DEPTH_LOG_ATTRIBUTES = {
            attribute("HoleDepth", "startIndex"),
            attribute("HoleDepth", "endIndex")
    };

    public static final UOMAttributeMetadata[] TORQUE_DRAG_WELL_ATTRIBUTES = {
            attribute("HoleDepth", "depth"),
            attribute("HookLoad", "slackOffWeight"),
            attribute("HookLoad", "pickUpWeight"),
            attribute("HookLoad", "dryWeight"),
            attribute("Torque", "torque"),
            attribute("HookLoad", "breakOverMin"),
            attribute("HookLoad", "breakOverMax"),
            attribute("HookLoad", "rotatingWeight")
    };

    public static final UOMAttributeMetadata[] TORQUE_DRAG_RUN_ATTRIBUTES = {
            attribute("HoleDepth", "holeDepth"),
            attribute("HoleDepth", "startHoleDepth")
    };

    public static final UOMAttributeMetadata[] GENERIC_TAD_ATTRIBUTES = {
            attribute("HoleDepth", "depth"),
            attribute("HookLoad", "slackOffWeightPumpsOn"),
            attribute("HookLoad", "slackOffWeightPumpsOff"),
            attribute("HookLoad", "maxSlackOffWeightPumpsOn"),
            attribute("HookLoad", "maxSlackOffWeightPumpsOff"),
            attribute("HookLoad", "pickUpWeightPumpsOn"),
            attribute("HookLoad", "pickUpWeightPumpsOff"),
            attribute("HookLoad", "maxPickUpWeightPumpsOn"),
            attribute("HookLoad", "maxPickUpWeightPumpsOff"),
            attribute("HookLoad", "rotatingWeightOnBottom"),
            attribute("HookLoad", "rotatingWeightOffBottom"),
            attribute("HookLoad", "rotatingWeightOnBottomSlipToBottom"),
            attribute("HookLoad", "rotatingWeightOffBottomSlipToBottom"),
            attribute("Torque", "torque"),
            attribute("Torque", "torqueSlipToBottom")
    };

    public static final UOMAttributeMetadata[] DRILLING_PROFILE_ATTRIBUTES = {
            attribute("HoleDepth", "holeDepth"),
            attribute("MudWeight", "mudWeight"),
    };

    public static final UOMAttributeMetadata[] SURVEY_RECORD_ATTRIBUTES = {
            attribute("SurveyMD", "previousMeasuredDepth"),
            attribute("SurveyMD", "curMeasuredDepth"),
            attribute("SurveyTVD", "tvd"),
            attribute("SurveyDLS", "dls"),
            attribute("BuildWalkRate", "bldRate"),
            attribute("BuildWalkRate", "wlkRate"),
            attribute("ROPAvg", "avgRopBySliding"),
            attribute("WeightOnBit", "avgWobBySliding"),
            attribute("DiffPressure", "avgDiffPressureBySliding"),
            attribute("Torque", "avgTorqueBySliding"),
            attribute("RPMA", "avgRpmBySliding"),
            attribute("MudFlowInAvg", "avgMudFlowInBySliding"),
            attribute("GammaRay1Corr", "avgGammaRay1ShiftedBySliding"),
            attribute("ROPAvg", "avgRopByRotaryDrilling"),
            attribute("WeightOnBit", "avgWobByRotaryDrilling"),
            attribute("DiffPressure", "avgDiffPressureByRotaryDrilling"),
            attribute("Torque", "avgTorqueByRotaryDrilling"),
            attribute("RPMA", "avgRpmByRotaryDrilling"),
            attribute("MudFlowInAvg", "avgMudFlowInByRotaryDrilling"),
            attribute("GammaRay1Corr", "avgGammaRay1ShiftedByRotaryDrilling"),
            attribute("CartesianCoordinates", "dispEW"),
            attribute("CartesianCoordinates", "dispNS")
    };

    public static final UOMAttributeMetadata[] COMPARISON_SUMMARY_VIEW_ATTRIBUTES = {
            attribute("ROPAvg", "parameters.avgROP"),
            attribute("WeightOnBit", "parameters.avgWOB"),
            attribute("RPMA", "parameters.avgRPM"),
            attribute("Torque", "parameters.avgTD"),
            attribute("DiffPressure", "parameters.avgDP"),
            attribute("MSE", "parameters.avgMSE"),
            attribute("MudFlowInAvg", "parameters.avgFlowRate")
    };

    public static final UOMAttributeMetadata[] TAD_BROOMSTICK_SERIES_ATTRIBUTES = {
    		attribute("HoleDepth", "holeDepth"),
            attribute("HookLoad", "value"),
    };

    public static final UOMAttributeMetadata[] DRILLER_DASHBOARD_PROFILE_ATTRIBUTES = {
            attribute("HoleDepth", "min"),
            attribute("HoleDepth", "max"),
    };

    public static final UOMAttributeMetadata[] KPI_TAG_ATTRIBUTES = {
            attribute("Depth", "start"),
            attribute("Depth", "end")
    };

    public static final UOMAttributeMetadata[] ROP_ALERT_ATTRIBUTES = {
            attribute("HoleDepth", "movingFootage"),
            attribute("HoleDepth", "offsetDetails.movingFootage")
    };

    public static final UOMAttributeMetadata[] DEPTH_ALERT_ATTRIBUTES = {
            attribute("HoleDepth", "depthValue")
    };

    public static class Range {

        private Double startIndex;
        private Double endIndex;

        public Range(MongoLog depthLog) {
            this(depthLog.getStartIndex(), depthLog.getEndIndex());
        }

        public Range(Double startIndex, Double endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public Range(Long startIndex, Long endIndex) {
            this.startIndex = startIndex.doubleValue();
            this.endIndex = endIndex.doubleValue();
        }

        public Range(Float startIndex, Float endIndex) {
            this.startIndex = startIndex.doubleValue();
            this.endIndex = endIndex.doubleValue();
        }

        public Double getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(Double startIndex) {
            this.startIndex = startIndex;
        }

        public Double getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(Double endIndex) {
            this.endIndex = endIndex;
        }
    }

    private static UOMConverter _converter = null;

    private static UOMConverter getInstance() {
        if (_converter == null) {
            _converter = new UOMConverter();
        }
        return _converter;
    }

    public static <T> List<T> convertFromMoblizeUnits(List<T> rawData, UOMAttributeMetadata ... attributes) {
        UOMConverter converter = getInstance();
        return converter.convertFromMoblizeUnits(rawData, attributes);
    }

    public static <T> List<T> convertToMoblizeUnits(List<T> rawData, UOMAttributeMetadata ... attributes) {
        UOMConverter converter = getInstance();
        return converter.convertToMoblizeUnits(rawData, attributes);
    }

    public static <T> T convertFromMoblizeUnits(T rawObject, UOMAttributeMetadata ... attributes) {
        UOMConverter converter = getInstance();
        List<T> rawData = new ArrayList<>();
        rawData.add(rawObject);
        List<T> convertedData = converter.convertFromMoblizeUnits(rawData, attributes);
        if (!convertedData.isEmpty()) {
            return convertedData.get(0);
        }
        return rawObject;
    }

    public static <T> T convertToMoblizeUnits(T rawObject, UOMAttributeMetadata ... attributes) {
        UOMConverter converter = getInstance();
        List<T> rawData = new ArrayList<>();
        rawData.add(rawObject);
        List<T> convertedData = converter.convertToMoblizeUnits(rawData, attributes);
        if (!convertedData.isEmpty()) {
            return convertedData.get(0);
        }
        return rawObject;
    }

    public static UOMAttributeMetadata attribute(String dataField, String jsonPath){
        return new UOMAttributeMetadata(dataField, jsonPath);
    }

    public static UOMAttributeMetadata[] rangeAttributes(String dataField){
        return new UOMAttributeMetadata[]{ attribute(dataField, "startIndex"), attribute(dataField, "endIndex")};
    }

}
