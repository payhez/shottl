package com.shuttler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttler.BaseTest;
import com.shuttler.controller.request.AddOrganisationRequest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.dao.ManagerRepository;
import com.shuttler.dao.OrganisationRepository;
import com.shuttler.model.Address;
import com.shuttler.model.Manager;
import com.shuttler.model.Organisation;
import com.shuttler.model.enums.OrganisationType;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

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

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post("/signup/manager")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(managerRequest)))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getAsyncResult(5000);

        Manager savedManager = (Manager) response.getBody();

       StepVerifier.create(managerRepository.findById(savedManager.getId()))
               .expectNextMatches(savedEntity -> savedEntity.getFirstName().equals("TheName"))
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

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post("/signup/manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testManagerSignUp_Duplication_on_Email_and_PhoneNumber() throws Exception {
        ManagerSignUpRequest managerInitialRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test.com")
                .phoneNumber("051353342")
                .password("TestPasswordWith1!")
                .build();

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

        ResponseEntity<?> responseForDuplicatedEmail = (ResponseEntity<?>)mockMvc.perform(post("/signup/manager")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(managerDuplicatedEmailAddressRequest)))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseForDuplicatedEmail.getStatusCode());

        ManagerSignUpRequest managerPhoneNumberRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test1.com")
                .phoneNumber("051353342")
                .password("TestPasswordWith1!")
                .build();

        ResponseEntity<?> responseForDuplicatedPhoneNumber = (ResponseEntity<?>)mockMvc.perform(post("/signup/manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDuplicatedEmailAddressRequest)))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn().getAsyncResult(5000);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseForDuplicatedPhoneNumber.getStatusCode());

    }

    @Test
    public void testOrganisationSignUp() throws Exception {
        Address address = Address.builder()
                .alley("The Alley")
                .city("The City")
                .postalCode("264").build();

        AddOrganisationRequest request = AddOrganisationRequest.builder()
                .name("Test Organisation")
                .address(address)
                .totalNumberOfPassengers(10)
                .geoLocation(new Point(25.5, 78.9))
                .organisationType(OrganisationType.SCHOOL).build();

        String responseAsString = mockMvc.perform(post("/signup/organisation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        Organisation savedOrganisation = objectMapper.readValue(responseAsString, Organisation.class);

        StepVerifier.create(organisationRepository.findById(savedOrganisation.getId()))
                .expectNextMatches(savedEntity -> savedEntity.getOrganisationName().equals("Test Organisation"))
                .expectComplete()
                .verify();
    }
}
