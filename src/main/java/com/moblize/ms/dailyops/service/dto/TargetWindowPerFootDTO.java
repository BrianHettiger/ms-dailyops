package com.moblize.ms.dailyops.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moblize.ms.dailyops.domain.mongo.Intersection;
import com.moblize.ms.dailyops.dto.TargetWindowsCoordinate;
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
    List<List<Float>> firstLine = new ArrayList<>();
    List<List<Float>> centerLine = new ArrayList<>();
    List<List<Float>> lastLine = new ArrayList<>();
    List<List<Float>> sideLine = new ArrayList<>();
    @ProtoField(number = 6, collectionImplementation = ArrayList.class)
    List<Intersection> svIntersections = new ArrayList<>();
    @ProtoField(number = 7, collectionImplementation = ArrayList.class)
    List<Intersection> pvIntersections = new ArrayList<>();

    @JsonIgnore
    @ProtoField(number = 8)
    TargetWindowsCoordinate firstLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 9)
    TargetWindowsCoordinate centerLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 10)
    TargetWindowsCoordinate lastLineCoordinate = new TargetWindowsCoordinate();
    @JsonIgnore
    @ProtoField(number = 11)
    TargetWindowsCoordinate sideLineCoordinate = new TargetWindowsCoordinate();


    public void setProtoData() {
        setProtoCoordData(firstLine, firstLineCoordinate);
        setProtoCoordData(centerLine, centerLineCoordinate);
        setProtoCoordData(lastLine, lastLineCoordinate);
        setProtoCoordData(sideLine, sideLineCoordinate);
    }

    public void setEntries() {
        setEntriesCoord(firstLineCoordinate, firstLine);
        setEntriesCoord(centerLineCoordinate, centerLine);
        setEntriesCoord(lastLineCoordinate, lastLine);
        setEntriesCoord(sideLineCoordinate, sideLine);
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


}
