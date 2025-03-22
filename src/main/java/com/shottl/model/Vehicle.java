package com.shottl.model;

import com.shottl.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private VehicleType vehicleType;
    private Short capacity;
    private String licencePlateNumber;
    private Point geoLocation;
}
