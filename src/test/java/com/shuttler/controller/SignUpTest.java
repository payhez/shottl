package com.shuttler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttler.BaseTest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
}
