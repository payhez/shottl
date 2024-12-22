package com.shuttler.model;

import com.shuttler.model.enums.InstitutionType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document("institution")
public class Institution {
    @Id
    private String id;

    @Indexed(unique = true)
    private String institutionName;

    private Address address;
    private InstitutionType institutionType;
    private Point geoLocation;
    private Integer totalNumberOfPassengers;
    private String email;
    private String phoneNumber;
    private List<String> managers;
    private String invitationCode;
}
