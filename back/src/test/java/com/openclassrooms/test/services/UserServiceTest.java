package com.openclassrooms.test.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    User mockUser = User.builder()
            .id(1L)
            .firstName("firstName")
            .lastName("lastName")
            .email("email@mail.com")
            .password("secretPassword")
            .admin(false)
            .build();

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository);
    }

    @Test
    void deleteShouldDeleteUser() {
        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void findByIdShouldReturnUserOrNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(mockUser));

        User result = userService.findById(1L);
        assertEquals(mockUser, result);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User emptyResult = userService.findById(1L);
        assertEquals(null, emptyResult);
    }
}
