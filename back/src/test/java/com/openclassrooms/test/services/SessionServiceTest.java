package com.openclassrooms.test.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    SessionService sessionService;

    @Mock
    SessionRepository sessionRepository;

    @Mock
    UserRepository userRepository;

    Teacher mockTeacher;
    Session mockSession;
    User mockUser;

    @BeforeEach
    void setup() {
        sessionService = new SessionService(sessionRepository, userRepository);

        mockTeacher = Teacher.builder()
                .lastName("teacherName")
                .firstName("teacherFirstName")
                .build();

        mockSession = Session.builder()
                .name("name")
                .date(new Date())
                .description("description")
                .teacher(mockTeacher)
                .users(new ArrayList<>())
                .createdAt(null)
                .build();

        mockUser = User.builder()
                .id(2L)
                .firstName("firstName")
                .lastName("lastName")
                .email("user@user.com")
                .admin(false)
                .password("password")
                .build();
    }

    @Test
    void createASessionShouldReturnTheSessionDetail() {
        when(sessionRepository.save(mockSession)).thenReturn(mockSession);

        Session result = sessionService.create(mockSession);

        verify(sessionRepository).save(mockSession);

        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(mockTeacher, result.getTeacher());
        assertEquals(0, result.getUsers().size());
    }

    @Test
    void deleteShouldDeleteTheSession() {
        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void findAllShouldReturnSessions() {
        List<Session> mockSessions = List.of(mockSession, mockSession);
        when(sessionRepository.findAll()).thenReturn(mockSessions);

        List<Session> result = sessionService.findAll();

        assertEquals(2, result.size());
        assertEquals(mockSessions, result);
    }

    @Test
    void getByIdShouldReturnTheSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));

        Session result = sessionService.getById(1L);

        assertEquals(mockSession, result);
    }

    @Test
    void updateShouldUpdateTheSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));
        when(sessionRepository.save(mockSession)).thenReturn(mockSession);

        Session result = sessionService.update(1L, mockSession);

        assertEquals(result, mockSession);
    }

    @Test
    void updateShouldThrowAnExceptionIfSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.update(1L, mockSession));
    }

    @Test
    void participateShouldAddTheUserToSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(mockUser));
        when(sessionRepository.save(mockSession)).thenReturn(mockSession);

        sessionService.participate(1L, 2L);

        verify(sessionRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(sessionRepository).save(mockSession);
        assertEquals(1, mockSession.getUsers().size());
        assertEquals(mockUser, mockSession.getUsers().get(0));
    }

    @Test
    void participateShouldThrowAnExceptionIfSessionOrUserDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(mockUser));

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));

        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void participateShouldThrowABadRequestExceptionIfUserAlreadyParticipate() {
        mockSession.getUsers().add(mockUser);

        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(mockUser));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void noLongerParticipateShouldRemoveUserFromSession() {
        mockSession.getUsers().add(mockUser);

        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));
        when(sessionRepository.save(mockSession)).thenReturn(mockSession);

        sessionService.noLongerParticipate(1L, 2L);

        verify(sessionRepository).findById(1L);
        verify(sessionRepository).save(mockSession);
        assertEquals(0, mockSession.getUsers().size());
    }

    @Test
    void noLongerParticipateShouldThrowANotFoundExceptionIfSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 2L));
    }

    @Test
    void noLongerParticipateShouldThrowABadRequestExceptionIfUserDoesNotParticipate() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.ofNullable(mockSession));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 2L));
    }
}
