package com.openclassrooms.test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    UserDetailsService userDetailsService;

    @Mock
    UserRepository userRepository;

    User fakeUser = User.builder()
            .id(1L)
            .email("yoga@studio.com")
            .lastName("lastName")
            .firstName("firstName")
            .password("password")
            .admin(false)
            .build();

    @BeforeEach
    void setup() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByNameShouldReturnUserDetails() {
        String email = "yoga@studio.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(fakeUser));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        verify(userRepository).findByEmail(email);

        assertEquals(fakeUser.getEmail(), result.getUsername());
        assertEquals(true, result.isAccountNonExpired());
        assertEquals(true, result.isAccountNonLocked());
        assertEquals(true, result.isCredentialsNonExpired());
        assertEquals(true, result.isEnabled());
    }

    @Test
    void loadUserByNameShouldThrowAnExceptionIfUserDoesNotExist() {
        String email = "yoga@studio.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));

        assertEquals("User Not Found with email: " + email, exception.getMessage());
    }
}