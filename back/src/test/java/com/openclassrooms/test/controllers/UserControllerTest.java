package com.openclassrooms.test.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    User mockUser = User.builder()
            .id(1L)
            .firstName("firstName")
            .lastName("lastName")
            .email("email@email.com")
            .password("secretPassword")
            .admin(false)
            .build();

    UserDto mockUserDto = UserDto.builder()
            .id(1L)
            .firstName("firstName")
            .lastName("lastName")
            .email("email@email.com")
            .admin(false)
            .build();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findByIdShouldReturnAnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"));
    }

    @Test
    void findByIdShouldReturn404WhenNoUser() throws Exception {
        when(userService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldDeleteTheUser() throws Exception {
        when(userService.findById(1L)).thenReturn(mockUser);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username("email@email.com")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }

    @Test
    void deleteShouldReturnNotFoundIfNoUser() throws Exception {
        when(userService.findById(1L)).thenReturn(null);
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn404WhenAuthUserIsNotTheDeletedOne() throws Exception {
        when(userService.findById(1L)).thenReturn(mockUser);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username("another@mail.com")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

}
