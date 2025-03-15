package com.moush.controller.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManagerSignUpRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
}
