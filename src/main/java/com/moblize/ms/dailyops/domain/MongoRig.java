package com.moblize.ms.dailyops.domain;
import com.moblize.ms.dailyops.dto.Location;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infinispan.protostream.annotations.ProtoField;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

@Document(value = "rigs")
@Data
@NoArgsConstructor
public class MongoRig {
    @Id
    private String id;
    private String name;
    private String uid;
    private String typeRig;
}

