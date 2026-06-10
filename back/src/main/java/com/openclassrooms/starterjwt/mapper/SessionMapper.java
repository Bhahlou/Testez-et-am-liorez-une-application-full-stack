package com.openclassrooms.starterjwt.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@Component
@Mapper(componentModel = "spring", uses = {UserService.class}, imports = {Arrays.class, Collectors.class, Session.class, User.class, Collections.class, Optional.class})
public abstract class SessionMapper implements EntityMapper<SessionDto, Session> {

    @Autowired
    TeacherService teacherService;
    @Autowired
    UserService userService;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(source = "description", target = "description"),
            @Mapping(target = "teacher", expression = "java(sessionDto.getTeacher_id() != null ? this.teacherService.findById(sessionDto.getTeacher_id()) : null)"),
            @Mapping(target = "users", expression = "java(Optional.ofNullable(sessionDto.getUsers()).orElseGet(Collections::emptyList).stream().map(user_id -> { User user = this.userService.findById(user_id); if (user != null) { return user; } return null; }).collect(Collectors.toList()))"),
    })
    
    @Override
    public abstract Session toEntity(SessionDto sessionDto);


    @Mappings({
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "session.teacher.id", target = "teacher_id"),
            @Mapping(target = "users", expression = "java(Optional.ofNullable(session.getUsers()).orElseGet(Collections::emptyList).stream().map(u -> u.getId()).collect(Collectors.toList()))"),
    })
    @Override
    public abstract SessionDto toDto(Session session);
}
