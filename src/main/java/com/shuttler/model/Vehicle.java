package com.shuttler.model;

import com.shuttler.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private VehicleType vehicleType;
    private Short capacity;
    private String licencePlateNumber;
}
