package com.shuttler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttler.BaseTest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.dao.ManagerRepository;
import com.shuttler.dao.OrganisationRepository;
import com.shuttler.model.Manager;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ManagerSignUpIT extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ManagerRepository managerRepository;

    private static final String URL_TEMPLATE = "/api/v1/manager/signup";

    @Test
    public void testManagerSignUp_HappyPath() throws Exception {
        ManagerSignUpRequest managerRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test.com")
                .phoneNumber("051353342")
                .password("TestPasswordWith1!")
                .build();

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(managerRequest)))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getAsyncResult(5000);

        Assertions.assertEquals("Manager saved successfully.", response.getBody());

        StepVerifier.create(managerRepository.findByFirstName(managerRequest.getFirstName()))
                .expectNextMatches(savedEntity -> savedEntity.getEmail().equals("test@test.com"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testManagerSignUp_Password_Validation_Fails() throws Exception {
        ManagerSignUpRequest managerRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test.com")
                .phoneNumber("+90123456789")
                .password("WeakPass")
                .build();

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    //@Ignore // TODO It fails for some reason.. The email and phoe number
    public void testManagerSignUp_Duplication_on_Email_and_PhoneNumber() throws Exception {

        Manager initialManager = Manager.builder()
                                    .firstName("TheName")
                                    .middleName("TheMiddleName")
                                    .surname("TheSurname")
                                    .email("test@test.com")
                                    .phoneNumber("051353342")
                                    .build();

        managerRepository.save(initialManager).block();

        ManagerSignUpRequest managerDuplicatedEmailAddressRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test.com")
                .phoneNumber("2222")
                .password("TestPasswordWith1!")
                .build();


        ResponseEntity<?> responseForDuplicatedEmail = (ResponseEntity<?>)mockMvc.perform(post(URL_TEMPLATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(managerDuplicatedEmailAddressRequest)))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.CONFLICT, responseForDuplicatedEmail.getStatusCode());

        ManagerSignUpRequest managerPhoneNumberRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test1.com")
                .phoneNumber("051353342")
                .password("TestPasswordWith1!")
                .build();

        ResponseEntity<?> responseForDuplicatedPhoneNumber = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerPhoneNumberRequest)))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.CONFLICT, responseForDuplicatedPhoneNumber.getStatusCode());

    }
}
