package com.shottl.controller;

import com.shottl.controller.api.v1.manager.ManagerSignUpController;
import com.shottl.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import static com.shottl.TestConstants.MANAGER_SIGNUP_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class ManagerSignUpControllerTest {

    private ManagerSignUpController sut;

    @Mock
    private ManagerService managerService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL_TEMPLATE = "/signup";

    @BeforeEach
    void setUp() {
        sut = new ManagerSignUpController(managerService);
        mockMvc = standaloneSetup(sut).build();
    }


    @Test
    void testHappyPath() throws Exception {

        when(managerService.addManager(any(), any())).thenReturn(Mono.empty());

        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(MANAGER_SIGNUP_REQUEST)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testError() throws Exception {

        when(managerService.addManager(any(), any()))
                .thenReturn(
                    Mono.error(
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "No communication channel is provided!")
                    )
                );

        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MANAGER_SIGNUP_REQUEST)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }
}
