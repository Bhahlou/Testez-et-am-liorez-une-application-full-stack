package com.openclassrooms.test.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.SpringBootSecurityJwtApplication;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class SessionControllerIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("oc.app.jwtSecret",
                () -> "RJG2XP/bcKd1hQKIKjnxBRhDb+mv+Gc1VT9Ib8odwnoT2mIkSJW1RmAta2gLoNaJ1w3I2dd1WNehQMfEXjtNSw==");
        registry.add("oc.app.jwtExpirationMs", () -> 86400000);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    Long existingSessionId;
    Long freeTeacherId;
    Long existingUserId;

    String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        User existingUser = User.builder()
                .email("existing@mail.com")
                .firstName("Existing")
                .lastName("User")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();
        existingUserId = userRepository.save(existingUser).getId();

        Teacher existingTeacher = Teacher.builder()
                .firstName("teacherFirstName")
                .lastName("teacherLastName")
                .build();

        Teacher existingTeacher2 = Teacher.builder()
                .firstName("teacher2FirstName")
                .lastName("teacher2LastName")
                .build();

        teacherRepository.save(existingTeacher);
        freeTeacherId = teacherRepository.save(existingTeacher2).getId();

        Session existingSession = Session.builder()
                .name("sessionName")
                .description("sessionDescription")
                .date(new Date())
                .teacher(existingTeacher)
                .users(new ArrayList<>())
                .build();

        existingSessionId = sessionRepository.save(existingSession).getId();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("existing@mail.com");
        loginRequest.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtToken = objectMapper.readValue(loginResponse, JwtResponse.class).getToken();
    }

    @Test
    void findByIdShouldReturnSession() throws Exception {
        mockMvc.perform(get("/api/session/" + existingSessionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingSessionId))
                .andExpect(jsonPath("$.name").value("sessionName"))
                .andExpect(jsonPath("$.description").value("sessionDescription"))
                .andExpect(jsonPath("$.teacher_id").exists())
                .andExpect(jsonPath("$.users").isEmpty())
                .andExpect(jsonPath("$.date").exists());
    }

    @Test
    void findByIdShouldReturnUnauthorizedWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/session/" + existingSessionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByIdShouldReturnBadRequestWhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/session/notANumber")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllShouldReturnAListOfSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(existingSessionId))
                .andExpect(jsonPath("$[0].name").value("sessionName"))
                .andExpect(jsonPath("$[0].description").value("sessionDescription"))
                .andExpect(jsonPath("$[0].teacher_id").exists())
                .andExpect(jsonPath("$[0].users").isEmpty())
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    void postSessionShouldCreateANewSession() throws Exception {
        SessionDto newSession = SessionDto.builder()
                .name("newSessionName")
                .description("newSessionDescription")
                .teacher_id(freeTeacherId)
                .date(new Date())
                .build();

        MvcResult result = mockMvc.perform(post("/api/session")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSession)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("newSessionName"))
                .andExpect(jsonPath("$.description").value("newSessionDescription"))
                .andExpect(jsonPath("$.teacher_id").value(freeTeacherId))
                .andExpect(jsonPath("$.users").isEmpty())
                .andExpect(jsonPath("$.date").exists())
                .andReturn();

        Long createdSessionId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        assertThat(sessionRepository.existsById(createdSessionId)).isTrue();
    }

    @Test
    void updateSessionShouldUpdateExistingSession() throws Exception {
        SessionDto updatedSession = SessionDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .date(new Date())
                .teacher_id(freeTeacherId)
                .build();

        mockMvc.perform(put("/api/session/" + existingSessionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSession)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingSessionId))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.description").value("updatedDescription"))
                .andExpect(jsonPath("$.teacher_id").value(freeTeacherId))
                .andExpect(jsonPath("$.users").isEmpty())
                .andExpect(jsonPath("$.date").exists())
                .andReturn();

        assertThat(sessionRepository.existsById(existingSessionId)).isTrue();
    }

    @Test
    void deleteSessionShouldDeleteExistingSessionIfExists() throws Exception {
        mockMvc.perform(delete("/api/session/32145")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

        assertThat(sessionRepository.existsById(existingSessionId)).isTrue();

        mockMvc.perform(delete("/api/session/" + existingSessionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());

        assertThat(sessionRepository.existsById(existingSessionId)).isFalse();
    }

    @Test
    void participateShouldAddUserToSession() throws Exception {
        mockMvc.perform(post("/api/session/" + existingSessionId + "/participate/" + existingUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());

        Optional<Session> session = sessionRepository.findById(existingSessionId);

        assertThat(session).isPresent();
        assertThat(session.get().getUsers().getFirst().getLastName()).isEqualTo("User");
        assertThat(session.get().getUsers().getFirst().getFirstName()).isEqualTo("Existing");

        mockMvc.perform(delete("/api/session/" + existingSessionId + "/participate/" + existingUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());

        session = sessionRepository.findById(existingSessionId);

        assertThat(session).isPresent();
        assertThat(session.get().getUsers()).isEmpty();
    }
}
