package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApplicationDto;
import org.example.exception.EntityNotFoundException;
import org.example.model.StatusApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.xml.bind.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ApplicationDto application;
    @BeforeEach
    public void addApplication(){
        application = ApplicationDto.builder()
                .description("проверка")
                .statusApplication(StatusApplication.DRAFT)
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAddApplication() throws Exception {
        mockMvc.perform(post("/application")
                .contentType(MediaType.APPLICATION_JSON)
                        .contentType(toJSonString(application)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lea", roles = {"ADMIN"})
    public void testAddApplicationError() throws Exception {
        mockMvc.perform(post("/application"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateStatusApplication() {
    }

    @Test
    void getAll() {
    }

    private static String toJSonString(Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}