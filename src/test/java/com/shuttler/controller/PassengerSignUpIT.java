package com.shuttler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttler.BaseTest;
import com.shuttler.controller.request.PassengerSignUpRequest;
import com.shuttler.dao.OrganisationRepository;
import com.shuttler.dao.PassengerRepository;
import com.shuttler.model.Organisation;
import com.shuttler.model.Passenger;
import com.shuttler.model.enums.OrganisationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    // TODO add passengers with invalid and empty invitation code Test
    // TODO sign up with inv. code that belongs to a non existing org
    // TODO Org. active num of passengers exceeded the total number

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
}
