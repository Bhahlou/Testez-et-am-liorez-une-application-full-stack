package com.openclassrooms.test.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.exception.MailAlreadyExistsException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        AuthService authService;

        @Mock
        AuthenticationManager authManager;

        @Mock
        JwtUtils jwtUtils;

        @Mock
        UserRepository userRepository;

        @Mock
        PasswordEncoder passwordEncoder;

        @BeforeEach
        void setup() {
                authService = new AuthService(
                                authManager,
                                jwtUtils,
                                userRepository,
                                passwordEncoder);
        }

        @Test
        void authenticateShouldReturnAValidJwtResponse() {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail("yoga@studio.com");
                loginRequest.setPassword("test!1234");

                UserDetailsImpl userDetails = UserDetailsImpl.builder()
                                .id(1L)
                                .username("yoga@studio.com")
                                .firstName("Admin")
                                .lastName("Admin")
                                .admin(true)
                                .password("encoded-password")
                                .build();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                User user = User.builder()
                                .id(1L)
                                .email("yoga@studio.com")
                                .firstName("Admin")
                                .lastName("Admin")
                                .password("encoded-password")
                                .admin(true)
                                .build();

                when(authManager.authenticate(any())).thenReturn(authentication);
                when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt-token");
                when(userRepository.findByEmail("yoga@studio.com")).thenReturn(Optional.of(user));

                JwtResponse response = authService.authenticateUser(loginRequest);

                assertEquals("fake-jwt-token", response.getToken());
                assertEquals(1L, response.getId());
                assertEquals("yoga@studio.com", response.getUsername());
                assertEquals("Admin", response.getFirstName());
                assertEquals("Admin", response.getLastName());
                assertEquals(true, response.getAdmin());
        }

        @Test
        void registerShouldRegisterTheUser() {
                SignupRequest signupRequest = new SignupRequest();
                signupRequest.setEmail("yoga@studio.com");
                signupRequest.setFirstName("FirstName");
                signupRequest.setLastName("LastName");
                signupRequest.setPassword("VerySecureP@ssword");

                when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
                when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encoded-password");

                User expectedUser = User.builder()
                                .email(signupRequest.getEmail())
                                .firstName(signupRequest.getFirstName())
                                .lastName(signupRequest.getLastName())
                                .password("encoded-password")
                                .admin(false)
                                .build();

                authService.register(signupRequest);
                verify(userRepository).save(expectedUser);
        }

        @Test
        void registerShouldThrowAnExceptionWhenEmailAlreadyExists() {
                SignupRequest signupRequest = new SignupRequest();
                signupRequest.setEmail("yoga@studio.com");
                signupRequest.setFirstName("FirstName");
                signupRequest.setLastName("LastName");
                signupRequest.setPassword("VerySecureP@ssword");

                when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);
                MailAlreadyExistsException exception = assertThrows(MailAlreadyExistsException.class,
                                () -> authService.register(signupRequest));

                assertEquals("Email already exists", exception.getMessage());

        }
}
