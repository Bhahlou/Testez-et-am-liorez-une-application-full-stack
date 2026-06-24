package com.openclassrooms.test.controllers;

import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TeacherController.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherMapper teacherMapper;

    @MockitoBean
    private TeacherService teacherService;

    private Teacher mockTeacher = Teacher.builder()
            .firstName("firstName")
            .lastName("lastName")
            .build();

    private TeacherDto mockTeacherDto = TeacherDto.builder()
            .firstName("firstName")
            .lastName("lastName")
            .build();

    @Test
    void findByIdShouldReturnATeacher() throws Exception {
        when(teacherService.findById(1L)).thenReturn(mockTeacher);
        when(teacherMapper.toDto(mockTeacher)).thenReturn(mockTeacherDto);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.lastName").value("lastName"));
    }

    @Test
    void findByIdShouldReturn404() throws Exception {
        when(teacherService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllShouldReturnAllTeachers() throws Exception {
        List<Teacher> mockTeachers = Arrays.asList(mockTeacher, mockTeacher);
        List<TeacherDto> mockTeachersDto = Arrays.asList(mockTeacherDto, mockTeacherDto);

        when(teacherService.findAll()).thenReturn(mockTeachers);
        when(teacherMapper.toDto(mockTeachers)).thenReturn(mockTeachersDto);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("firstName"))
                .andExpect(jsonPath("$[0].lastName").value("lastName"))
                .andExpect(jsonPath("$[1].firstName").value("firstName"))
                .andExpect(jsonPath("$[1].lastName").value("lastName"));
    }
}
