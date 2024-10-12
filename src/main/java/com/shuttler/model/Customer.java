package com.shuttler.model;

import com.shuttler.model.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String name;
    private Address address;
    private CustomerType customerType;
    private Point geoLocation;
}
