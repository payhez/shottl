package com.shuttler.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
public class Person {
    private String firstName;
    private String middleName;
    private String surname;
    private Date birthDate;
    private Address address;
    private Point geoLocation;

    @Indexed(unique = true, sparse = true)
    private String email;

    @Indexed(unique = true, sparse = true)
    private String phoneNumber;
}
