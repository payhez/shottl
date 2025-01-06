package com.shuttler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttler.BaseTest;
import com.shuttler.controller.request.AddInstitutionRequest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.dao.InstitutionRepository;
import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Address;
import com.shuttler.model.Institution;
import com.shuttler.model.Manager;
import com.shuttler.model.enums.InstitutionType;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    private InstitutionRepository institutionRepository;

    @Test
    public void testManagerSignUp() throws Exception {
        ManagerSignUpRequest managerRequest = ManagerSignUpRequest.builder()
                .firstName("TheName")
                .middleName("TheMiddleName")
                .lastName("TheSurname")
                .email("test@test.com")
                .phoneNumber("051353342")
                .build();

        mockMvc.perform(post("/manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerRequest)))
                .andExpect(status().is2xxSuccessful());

        List<Manager> managers = managerRepository.findAll();
        Assertions.assertTrue(CollectionUtils.isNotEmpty(managers));
        Assertions.assertEquals(1, managers.size());
    }

    @Test
    public void testInstitutionSignUp() throws Exception {
        Address address = Address.builder()
                .alley("Thealley")
                .city("The City")
                .postalCode("264").build();

        AddInstitutionRequest request = AddInstitutionRequest.builder()
                .name("Test Institution")
                .address(address)
                .totalNumberOfPassengers(10)
                .geoLocation(new Point(25.5, 78.9))
                .institutionType(InstitutionType.SCHOOL).build();

        mockMvc.perform(post("/institution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());

        List<Institution> institutions = institutionRepository.findAll();
        Assertions.assertTrue(CollectionUtils.isNotEmpty(institutions));
        Assertions.assertEquals(1, institutions.size());
    }
}
