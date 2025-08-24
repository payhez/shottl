package com.shottl;

import com.shottl.controller.request.ManagerSignUpRequest;

public class TestConstants {
    public static ManagerSignUpRequest MANAGER_SIGNUP_REQUEST = ManagerSignUpRequest.builder()
            .firstName("TheName")
            .middleName("TheMiddleName")
            .lastName("TheSurname")
            .email("test@test.com")
            .phoneNumber("051353342")
            .password("TestPasswordWith1!")
            .build();
}
