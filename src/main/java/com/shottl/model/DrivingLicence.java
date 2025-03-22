package com.shottl.model;

import com.shottl.model.enums.DrivingLicenceType;
import lombok.Data;

import java.util.Date;

@Data
public class DrivingLicence {
    private String licenceId;
    private String issuedBy;
    private Date issueDate;
    private Date dueDate;
    private DrivingLicenceType licenceType;

}
