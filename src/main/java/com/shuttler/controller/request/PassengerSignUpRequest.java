package com.shuttler.controller.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
@Builder
public class PassengerSignUpRequest {
    private String invitationCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Point location;
    private String password;
}
