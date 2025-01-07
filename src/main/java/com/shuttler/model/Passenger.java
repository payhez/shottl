package com.shuttler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Passenger extends Person {
    @Id
    private String id;

    private Date signUpDate;
    private Date updateDate;
    private String organisationId;
    private Boolean active;
}
