package com.moush.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moush.BaseTest;
import com.moush.controller.request.PassengerSignUpRequest;
import com.moush.dao.OrganisationRepository;
import com.moush.dao.PassengerRepository;
import com.moush.model.Organisation;
import com.moush.model.enums.OrganisationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PassengerSignUpIT extends BaseTest {

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    OrganisationRepository organisationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_TEMPLATE = "/api/v1/passenger/signup";

    @Test
    public void passengerSignUp_HappyPath() throws Exception {

        String invitationCode = UUID.randomUUID().toString();
        Organisation mockOrg  = Organisation.builder()
                                    .organisationName("Test Org")
                                    .totalNumberOfPassengers(10)
                                    .activeNumberOfPassengers(5)
                                    .organisationType(OrganisationType.COMPANY)
                                    .invitationCode(invitationCode)
                                    .build();
        organisationRepository.save(mockOrg).subscribe();

        PassengerSignUpRequest request = PassengerSignUpRequest.builder()
                                            .email("test@passenger.com")
                                            .firstName("Test")
                                            .lastName("Passenger")
                                            .invitationCode(invitationCode)
                                            .password("TestPassword1!")
                                            .build();

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals("Passenger saved successfully.", response.getBody());

        StepVerifier.create(passengerRepository.findByFirstName(request.getFirstName()))
                .expectNextMatches(savedEntity -> savedEntity.getEmail().equals("test@passenger.com"))
                .expectComplete()
                .verify();
    }

    @Test
    public void passengerSignUp_Invalid_Empty_Inv_Code() throws Exception {

        PassengerSignUpRequest request = PassengerSignUpRequest.builder()
                .email("test@passenger.com")
                .firstName("Test")
                .lastName("Passenger")
                .invitationCode("")
                .password("TestPassword1!")
                .build();

        ResponseEntity<?> responseEmptyInvCode = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEmptyInvCode.getStatusCode());
        Assertions.assertEquals("Invitation code required", responseEmptyInvCode.getBody());

        request.setInvitationCode("SomeInvalidInvitationCode");

        ResponseEntity<?> responseInvalidInvCode = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseInvalidInvCode.getStatusCode());
        Assertions.assertEquals("Invalid code.", responseInvalidInvCode.getBody());
    }

    @Test
    public void passengerSignUp_NonExistent_Organisation() throws Exception {

        String invitationCode = UUID.randomUUID().toString();

        PassengerSignUpRequest request = PassengerSignUpRequest.builder()
                .email("test@passenger.com")
                .firstName("Test")
                .lastName("Passenger")
                .invitationCode(invitationCode)
                .password("TestPassword1!")
                .build();

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Organisation not found!", response.getBody());

        StepVerifier.create(passengerRepository.findByFirstName(request.getFirstName()))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void passengerSignUp_exceedingNumberOfPassengers() throws Exception {

        String invitationCode = UUID.randomUUID().toString();
        Organisation mockOrg  = Organisation.builder()
                .organisationName("Test Org")
                .totalNumberOfPassengers(10)
                .activeNumberOfPassengers(10)
                .organisationType(OrganisationType.COMPANY)
                .invitationCode(invitationCode)
                .build();
        organisationRepository.save(mockOrg).subscribe();

        PassengerSignUpRequest request = PassengerSignUpRequest.builder()
                .email("test@passenger.com")
                .firstName("Test")
                .lastName("Passenger")
                .invitationCode(invitationCode)
                .password("TestPassword1!")
                .build();

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals("No more passengers allowed.", response.getBody());

        StepVerifier.create(passengerRepository.findByFirstName(request.getFirstName()))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }
}
