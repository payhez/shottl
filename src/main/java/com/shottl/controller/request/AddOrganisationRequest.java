package com.shottl.controller.request;

import com.shottl.model.Address;
import com.shottl.model.enums.OrganisationType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
@Builder
public class AddOrganisationRequest {
    private String name;
    private Address address;
    private Integer totalNumberOfPassengers;
    private Point geoLocation;
    private OrganisationType organisationType;
    private String managerId;
}
