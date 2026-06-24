package com.openclassrooms.test.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = SessionController.class)
class SessionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SessionService sessionService;

    @MockitoBean
    SessionMapper sessionMapper;

    private Teacher mockTeacher = Teacher.builder()
            .firstName("firstName")
            .lastName("lastName")
            .build();
    private User mockUser = User.builder()
            .firstName("firstName")
            .lastName("lastName")
            .password("password")
            .email("user@mail.com")
            .build();

    private Session mockSession = Session.builder()
            .id(1L)
            .name("sessionName")
            .date(new Date())
            .teacher(mockTeacher)
            .description("sessionDescription")
            .users(List.of(mockUser))
            .build();

    private SessionDto mockSessionDto = SessionDto.builder()
            .id(1L)
            .name("sessionName")
            .date(new Date())
            .teacher_id(1L)
            .description("sessionDescription")
            .users(List.of(1L))
            .build();

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void findByIdShouldReturnASessionDetail() throws Exception {
        when(sessionService.getById(1L)).thenReturn(mockSession);
        when(sessionMapper.toDto(mockSession)).thenReturn(mockSessionDto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("sessionName"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.teacher_id").value(1))
                .andExpect(jsonPath("$.description").value("sessionDescription"))
                .andExpect(jsonPath("$.users").value(1));
    }

    @Test
    void findByIdShouldReturn404IfSessionNotFound() throws Exception {
        when(sessionService.getById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllShouldReturnAllSessionDetail() throws Exception {
        List<Session> mockList = List.of(mockSession, mockSession);
        when(sessionService.findAll()).thenReturn(mockList);

        List<SessionDto> mockDtos = List.of(mockSessionDto, mockSessionDto);
        when(sessionMapper.toDto(mockList)).thenReturn(mockDtos);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("sessionName"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].teacher_id").value(1))
                .andExpect(jsonPath("$[0].description").value("sessionDescription"))
                .andExpect(jsonPath("$[0].users").value(1))
                .andExpect(jsonPath("$[1].name").value("sessionName"))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].teacher_id").value(1))
                .andExpect(jsonPath("$[1].description").value("sessionDescription"))
                .andExpect(jsonPath("$[1].users").value(1));
    }

    @Test
    void createShouldCreateASession() throws Exception {
        when(sessionMapper.toEntity(mockSessionDto)).thenReturn(mockSession);
        when(sessionMapper.toDto(mockSession)).thenReturn(mockSessionDto);
        when(sessionService.create(mockSession)).thenReturn(mockSession);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(mockSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("sessionName"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.teacher_id").value(1))
                .andExpect(jsonPath("$.description").value("sessionDescription"))
                .andExpect(jsonPath("$.users").value(1));
    }

    @Test
    void putShouldUpdateSession() throws Exception {
        when(sessionMapper.toEntity(mockSessionDto)).thenReturn(mockSession);
        when(sessionMapper.toDto(mockSession)).thenReturn(mockSessionDto);
        when(sessionService.update(1L, mockSession)).thenReturn(mockSession);

        mockMvc.perform(put("/api/session/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(mockSessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("sessionName"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.teacher_id").value(1))
                .andExpect(jsonPath("$.description").value("sessionDescription"))
                .andExpect(jsonPath("$.users").value(1));
    }

    @Test
    void deleteShouldDeleteSession() throws Exception {
        when(sessionService.getById(1L)).thenReturn(mockSession);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());

        verify(sessionService).delete(1L);
    }

    @Test
    void deleteShouldReturn404IfSessionDoesNotExist() throws Exception {
        when(sessionService.getById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void participateShouldAddParticipantToSession() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/1"))
                .andExpect(status().isOk());

        verify(sessionService).participate(1L, 1L);
    }

    @Test
    void unparticipateShouldRemoveParticipateFromSession() throws Exception {
        mockMvc.perform(delete("/api/session/1/participate/1"))
                .andExpect(status().isOk());

        verify(sessionService).noLongerParticipate(1L, 1L);
    }
}
