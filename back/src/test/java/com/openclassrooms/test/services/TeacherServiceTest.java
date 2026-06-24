package com.openclassrooms.test.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {
    TeacherService teacherService;

    @Mock
    TeacherRepository teacherRepository;

    List<Teacher> mockedTeachers = List.of(
            Teacher.builder().firstName("firstName1").lastName("lastName1").build(),
            Teacher.builder().firstName("firstName2").lastName("lastName2").build());

    @BeforeEach
    void setup() {
        teacherService = new TeacherService(teacherRepository);
    }

    @Test
    void findAllShouldReturnAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(mockedTeachers);

        List<Teacher> result = teacherService.findAll();
        assertEquals(2, result.size());
        assertEquals(mockedTeachers, result);
    }

    @Test
    void findByIdShouldReturnATeacher() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.ofNullable(mockedTeachers.get(0)));

        Teacher result = teacherService.findById(1L);
        assertEquals(mockedTeachers.get(0), result);

        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        Teacher emptyResult = teacherService.findById(1L);
        assertEquals(null, emptyResult);
    }
}
