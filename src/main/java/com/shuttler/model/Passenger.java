package com.shuttler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class Passenger extends Person {
    private Date signUpDate;
    private Date customerId;
    private Boolean active;
}
