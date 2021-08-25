package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moblize.ms.dailyops.domain.mongo.Intersection;
import com.moblize.ms.dailyops.dto.SideLinesCoordinates;
import com.moblize.ms.dailyops.dto.TargetWindowsCoordinate;
import com.moblize.ms.dailyops.dto.TargetWindowsSideLineCoordinate;
import com.moblize.ms.dailyops.dto.WindowCoordinate;
import lombok.*;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TargetWindowPerFootDTO {
    @ProtoField(number = 1)
    String wellUid;
    @ProtoField(number = 2)
    String customer;
    @ProtoField(number = 3)
    String wellStatus;
    @ProtoField(number = 4, collectionImplementation = ArrayList.class)
    List<ScaledTargetWindow> basic = new ArrayList<>();
    @ProtoField(number = 5, collectionImplementation = ArrayList.class)
    List<ScaledTargetWindow> advance = new ArrayList<>();

    List<List<Float>> pvFirstLine = new ArrayList<>();
    List<List<Float>> pvCenterLine = new ArrayList<>();
    List<List<Float>> pvLastLine = new ArrayList<>();
    List<List<List<Float>>> pvSideLine = new ArrayList<>();

    List<List<Float>> svFirstLine = new ArrayList<>();
    List<List<Float>> svCenterLine = new ArrayList<>();
    List<List<Float>> svLastLine = new ArrayList<>();
    List<List<List<Float>>> svSideLine = new ArrayList<>();

    @ProtoField(number = 6, collectionImplementation = ArrayList.class)
    List<Intersection> svIntersections = new ArrayList<>();
    @ProtoField(number = 7, collectionImplementation = ArrayList.class)
    List<Intersection> pvIntersections = new ArrayList<>();

    @JsonIgnore
    @ProtoField(number = 8)
    TargetWindowsCoordinate pvFirstLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 9)
    TargetWindowsCoordinate pvCenterLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 10)
    TargetWindowsCoordinate pvLastLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 11)
    TargetWindowsSideLineCoordinate pvSideLineCoordinate = new TargetWindowsSideLineCoordinate();
    @JsonIgnore
    @ProtoField(number = 12)
    TargetWindowsCoordinate svFirstLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 13)
    TargetWindowsCoordinate svCenterLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 14)
    TargetWindowsCoordinate svLastLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 15)
    TargetWindowsSideLineCoordinate svSideLineCoordinate = new TargetWindowsSideLineCoordinate();


    public void setProtoData() {
        setProtoCoordData(pvFirstLine, pvFirstLineCoordinate);
        setProtoCoordData(pvCenterLine, pvCenterLineCoordinate);
        setProtoCoordData(pvLastLine, pvLastLineCoordinate);
        setProtoCoordData(pvSideLine, pvSideLineCoordinate);
        setProtoCoordData(svFirstLine, svFirstLineCoordinate);
        setProtoCoordData(svCenterLine, svCenterLineCoordinate);
        setProtoCoordData(svLastLine, svLastLineCoordinate);
        setProtoCoordData(svSideLine, svSideLineCoordinate);
    }

    public void setEntries() {
        setEntriesCoord(pvFirstLineCoordinate, pvFirstLine);
        setEntriesCoord(pvCenterLineCoordinate, pvCenterLine);
        setEntriesCoord(pvLastLineCoordinate, pvLastLine);
        setEntriesCoord(pvSideLineCoordinate, pvSideLine);
        setEntriesCoord(svFirstLineCoordinate, svFirstLine);
        setEntriesCoord(svCenterLineCoordinate, svCenterLine);
        setEntriesCoord(svLastLineCoordinate, svLastLine);
        setEntriesCoord(svSideLineCoordinate, svSideLine);
    }

    public void setProtoCoordData(List<List<Float>> input, TargetWindowsCoordinate output) {
        if(output.getWindowCoordinates() .isEmpty() && !input.isEmpty()) {
            input.forEach((values) -> {
                if(!values.isEmpty()) {
                    WindowCoordinate windowCoordinate = new WindowCoordinate();
                    windowCoordinate.setXAxis(values.get(0));
                    windowCoordinate.setYAxis(values.get(1));
                    output.getWindowCoordinates().add(windowCoordinate);
                }
            });
        }
    }

    public void setEntriesCoord(TargetWindowsCoordinate input, List<List<Float>> output) {
        if(output.isEmpty() && !input.getWindowCoordinates().isEmpty()) {
            input.getWindowCoordinates().forEach(location ->{
                List<Float> coordinate = new ArrayList<>();
                coordinate.add(location.getXAxis());
                coordinate.add(location.getYAxis());
                output.add(coordinate);
            });
        }
    }
    public void setProtoCoordData(List<List<List<Float>>> input, TargetWindowsSideLineCoordinate output) {
        if (output.getSideLinesCoordinates().isEmpty() && !input.isEmpty()) {
            input.forEach((values) -> {
                SideLinesCoordinates sideLinesCoordinates = new SideLinesCoordinates();
                if (!values.isEmpty()) {
                    TargetWindowsCoordinate targetWindowsCoordinate = new TargetWindowsCoordinate();
                    values.forEach(sideLines -> {
                        WindowCoordinate windowCoordinate = new WindowCoordinate();
                        windowCoordinate.setXAxis(sideLines.get(0));
                        windowCoordinate.setYAxis(sideLines.get(1));
                        targetWindowsCoordinate.getWindowCoordinates().add(windowCoordinate);
                    });
                    sideLinesCoordinates.getWindowCoordinates().add(targetWindowsCoordinate);
                }
                output.getSideLinesCoordinates().add(sideLinesCoordinates);
            });
        }
    }

    public void setEntriesCoord(TargetWindowsSideLineCoordinate input, List<List<List<Float>>> output) {

        if (output.isEmpty() && !input.getSideLinesCoordinates().isEmpty()) {
            input.getSideLinesCoordinates().forEach(sideLinesCoordinates -> {
                List<List<Float>> sideLines = new ArrayList<>();
                sideLinesCoordinates.getWindowCoordinates().forEach(targetWindowsCoordinate -> {
                    targetWindowsCoordinate.getWindowCoordinates().forEach(windowCoordinate -> {
                        List<Float> coordinate = new ArrayList<>();
                        coordinate.add(windowCoordinate.getXAxis());
                        coordinate.add(windowCoordinate.getYAxis());
                        sideLines.add(coordinate);
                    });
                    output.add(sideLines);
                });
            });
        }
    }


}
