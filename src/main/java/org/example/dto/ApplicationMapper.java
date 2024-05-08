package org.example.dto;

import lombok.experimental.UtilityClass;
import org.example.model.Application;
import org.example.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ApplicationMapper {

    public Application toApplication(ApplicationDto applicationDto, LocalDateTime localDateTime, User idUser) {
        return Application.builder()
                .description(applicationDto.getDescription())
                .statusApplication(applicationDto.getStatusApplication())
                .user(idUser)
                .timeCreate(localDateTime)
                .build();
    }

    public ApplicationDto toApplicationDto(Application application) {
        return ApplicationDto.builder()
                .description(application.getDescription())
                .statusApplication(application.getStatusApplication())
                .build();
    }

    public List<ApplicationDto> toListApplicationDto(List<Application> applications) {
        return applications.stream()
                .map(ApplicationMapper::toApplicationDto)
                .collect(Collectors.toList());
    }
}
