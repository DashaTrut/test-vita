package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ApplicationDto;
import org.example.dto.ApplicationUpdateDto;
import org.example.exception.EntityNotFoundException;
import org.example.model.StatusApplication;
import org.example.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;
    private ApplicationUpdateDto applicationUpdateDto;

    private ApplicationDto application;

    @BeforeEach
    public void addApplication() {
        application = ApplicationDto.builder()
                .description("check")
                .statusApplication(StatusApplication.DRAFT)
                .build();
        applicationUpdateDto = ApplicationUpdateDto.builder()
                .statusApplication(StatusApplication.ACCEPTED)
                .build();
    }

    @Test
    @WithMockUser(username = "valy", roles = {"USER"})
    public void testAddApplication() throws Exception {
        when(applicationService.create(application, 2)).thenReturn(application);
        mockMvc.perform(post("/application")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(application))
                        .header(("X-Sharer-User-Id"), 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lea", roles = {"ADMIN"})
    public void testAddApplicationError() throws Exception {
        mockMvc.perform(post("/application")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(application))
                        .header(("X-Sharer-User-Id"), 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "valy", roles = {"USER"})
    void updateStatusApplication() throws Exception {
        int idApplication = 1;
        when(applicationService.updateStatusOrDescription(applicationUpdateDto, 2, idApplication)).thenReturn(application);
        mockMvc.perform(patch("/application/update/{idApplication}", idApplication)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(applicationUpdateDto))
                        .header(("X-Sharer-User-Id"), 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lea", roles = {"ADMIN"})
    void updateStatusApplicationAdmin() throws Exception {
        int idApplication = 1;
        when(applicationService.updateStatusOrDescription(applicationUpdateDto, 2, idApplication)).thenReturn(application);
        mockMvc.perform(patch("/application/update/{idApplication}", idApplication)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(applicationUpdateDto))
                        .header(("X-Sharer-User-Id"), 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "lea", roles = {"ADMIN"})
    void getAllAdmin() throws Exception {
        mockMvc.perform(get("/application/all")
                        .header(("X-Sharer-User-Id"), 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "valy", roles = {"USER"})
    void getAllUser() throws Exception {
        when(applicationService.getAllForUser(2, 0, 5, true, null)).thenReturn(List.of(application));
        mockMvc.perform(get("/application/all")
                        .header(("X-Sharer-User-Id"), 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }


    private static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

}