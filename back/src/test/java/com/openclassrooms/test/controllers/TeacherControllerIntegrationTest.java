package com.openclassrooms.test.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.SpringBootSecurityJwtApplication;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@Testcontainers
@Transactional
@AutoConfigureMockMvc
class TeacherControllerIntegrationTest {
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
    PasswordEncoder passwordEncoder;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    ObjectMapper objectMapper;

    Long existingUserId;
    Long existingTeacherId;
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

        Teacher mockTeacher = Teacher.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build();

        existingTeacherId = teacherRepository.save(mockTeacher).getId();

        Teacher secondMockTeacher = Teacher.builder()
                .firstName("secondFirstName")
                .lastName("secondLastName")
                .build();

        teacherRepository.save(secondMockTeacher);
    }

    @Test
    void findByIdShouldReturnTeacherDetail() throws Exception {
        mockMvc.perform(get("/api/teacher/" + existingTeacherId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.id").value(existingTeacherId))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void findAllShouldReturnAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("firstName"))
                .andExpect(jsonPath("$[0].lastName").value("lastName"))
                .andExpect(jsonPath("$[1].firstName").value("secondFirstName"))
                .andExpect(jsonPath("$[1].lastName").value("secondLastName"));
    }
}
