package com.shuttler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttler.BaseTest;
import com.shuttler.controller.request.AddOrganisationRequest;
import com.shuttler.dao.OrganisationRepository;
import com.shuttler.model.Address;
import com.shuttler.model.Organisation;
import com.shuttler.model.enums.OrganisationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationAdditionIT extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

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
