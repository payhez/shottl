package com.shuttler.model;

import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.Date;

@Data
public class Person {
    private String firstName;
    private String middleName;
    private String surname;
    private Date birthDate;
    private Address address;
    private Point geoLocation;
}
