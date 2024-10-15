package com.shuttler.model;

import com.shuttler.model.enums.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String institutionName;
    private Address address;
    private InstitutionType institutionType;
    private Point geoLocation;
}
