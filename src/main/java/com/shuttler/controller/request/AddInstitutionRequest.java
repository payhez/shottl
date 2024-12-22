package com.shuttler.controller.request;

import com.shuttler.model.Address;
import com.shuttler.model.enums.InstitutionType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
@Builder
public class AddInstitutionRequest {
    private String name;
    private Address address;
    private Integer totalNumberOfPassengers;
    private Point geoLocation;
    private InstitutionType institutionType;
    private String managerId;
}
