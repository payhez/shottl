package com.shuttler.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Address {
    private String doorNumber;
    private String street;
    private String alley;
    private String district;
    private String apartmentNumber;
    private String buildingName;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInfo;
}
