package com.moblize.ms.dailyops.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data @NoArgsConstructor
public class DepthCoordinate {
    @ProtoField(number = 1)
    Double depth;
    List<List<Float>> coordinates;
    @ProtoField(number = 2, collectionImplementation = ArrayList.class)
    List<Location> protoCoordiantes;

    public void setProtoData() {
        if(protoCoordiantes == null) {
            protoCoordiantes = new ArrayList<>();
            coordinates.forEach((values) -> {
                if(!values.isEmpty()) {
                    Location location = new Location();
                    location.setLat(values.get(0));
                    location.setLng(values.get(1));
                }
            });
        }
    }
    public void setEntries() {
        if(coordinates == null && protoCoordiantes != null) {
            coordinates = new ArrayList<>();
            protoCoordiantes.forEach(location ->{
                List<Float> coordinate = new ArrayList<>();
                coordinate.add(location.getLat());
                coordinate.add(location.getLng());
                coordinates.add(coordinate);
            });
        }
    }
}
