package com.shuttler.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String firstName;
    private String middleName;
    private String lastName;
    private Date birthDate;
    private Address address;
    private Point geoLocation;
    private Date signUpDate;
    private Date updateDate;
    private Boolean active;

    @Indexed(unique = true, sparse = true)
    private String email;

    @Indexed(unique = true, sparse = true)
    private String phoneNumber;
}
