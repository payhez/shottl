package com.shuttler.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.geo.Point;

import java.util.Date;

@Data
@SuperBuilder
public class Person {
    private String firstName;
    private String middleName;
    private String surname;
    private Date birthDate;
    private Address address;
    private Point geoLocation;
}
