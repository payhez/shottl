package com.moush.model;

import com.moush.model.enums.OrganisationType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document("organisation")
public class Organisation {
    @Id
    private String id;

    @Indexed(unique = true)
    private String organisationName;

    private Address address;
    private OrganisationType organisationType;
    private Point geoLocation;
    private Integer totalNumberOfPassengers;
    private Integer activeNumberOfPassengers;
    private String email;
    private String phoneNumber;
    private List<String> managers;
    private String invitationCode;
}
