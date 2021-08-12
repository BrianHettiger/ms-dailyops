package com.moblize.ms.dailyops.domain;
import com.moblize.ms.dailyops.dto.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embedded;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Document(collection = "wells")
public class MongoWell {
    @Id
    @ProtoField(number = 1)
    String id;
    @ProtoField(number = 2)
    String uid;
    @ProtoField(number = 3)
    String name;
    @ProtoField(number = 4)
    Boolean isHidden = false;
    @ProtoField(number = 5)
    String customer;
    @ProtoField(number = 6)
    String statusWell;
    @ProtoField(number = 7)
    String timeZone;
    @ProtoField(number = 8)
    Long completedAt;
    @ProtoField(number = 9)
    String country;
    @ProtoField(number = 10)
    String state;
    @ProtoField(number = 11)
    String district;
    @ProtoField(number = 12)
    String county;
    @ProtoField(number = 13)
    @Embedded
    DaysVsDepthAdjustmentDates daysVsDepthAdjustmentDates;
    @ProtoField(number = 14)
    @Embedded
    Location location;
    @Embedded
    Distance dist;
    @ProtoField(number = 15, collectionImplementation = ArrayList.class)
    @Embedded
    List<Rig> rigs = new ArrayList<>();

}

