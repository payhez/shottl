package com.shuttler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Driver extends Person {
    private String driverLicenceId;
}
