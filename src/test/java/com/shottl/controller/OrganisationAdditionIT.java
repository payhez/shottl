package com.shottl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shottl.BaseTest;
import com.shottl.controller.request.AddOrganisationRequest;
import com.shottl.dao.OrganisationRepository;
import com.shottl.model.Address;
import com.shottl.model.Organisation;
import com.shottl.model.enums.OrganisationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrganisationAdditionIT extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    private static final String URL_TEMPLATE = "/api/v1/organisation/add";

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

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .with(jwt().jwt(jwt -> jwt.subject("test-manager")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals("Organisation saved successfully.", response.getBody());

        StepVerifier.create(organisationRepository.findByOrganisationName(request.getName()))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @Test
    public void testOrganisationSignUp_Duplication() throws Exception {

        Organisation mockOrg = Organisation.builder()
                        .organisationType(OrganisationType.COMPANY)
                                .organisationName("Test Organisation")
                                        .build();

        organisationRepository.save(mockOrg).subscribe();

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

        ResponseEntity<?> response = (ResponseEntity<?>) mockMvc.perform(post(URL_TEMPLATE)
                        .with(jwt().jwt(jwt -> jwt.subject("test-manager")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getAsyncResult(5000);

        Assertions.assertEquals("Organisation saved successfully.", response.getBody());
    }
}
