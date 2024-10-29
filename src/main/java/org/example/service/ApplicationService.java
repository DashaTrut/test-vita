package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApplicationDto;
import org.example.dto.ApplicationMapper;
import org.example.dto.ApplicationUpdateDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.ValidationException;
import org.example.model.Application;
import org.example.model.StatusApplication;
import org.example.model.User;
import org.example.repository.ApplicationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final UserService userService;
    private final ApplicationRepository applicationRepository;

    private final Sort timeAsc = Sort.by(Sort.Direction.ASC, "timeCreate"); //по возрастанию
    private final Sort timeDesc = Sort.by(Sort.Direction.DESC, "timeCreate"); //по убыванию

    public ApplicationDto create(ApplicationDto applicationDto, Integer idUser) {
        User user = userService.getUser(idUser); //проверяем что такой пользователь существует
        try {
            if (applicationDto.getStatusApplication().equals(StatusApplication.ACCEPTED)
                    || applicationDto.getStatusApplication().equals(StatusApplication.REJECTED)) {
                throw new ValidationException("Неверный статус");
            }
            Application application = ApplicationMapper.toApplication(applicationDto, LocalDateTime.now(), user);
            Application applicationSave = applicationRepository.save(application);
            return ApplicationMapper.toApplicationDto(applicationSave);
        } catch (Exception e) {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public List<ApplicationDto> getAllForUser(int idUser, int from, int size, Boolean asc, String search) {
        Pageable pageable = pageableOf(from, size, asc);
        if (checkOperator(idUser)) {
            if (search == null || search.isBlank()) {
                return getAllOperator(from, size, asc);
            } //получение оператором списка заявок по части имени
            List<User> list = userService.searchUser(search, idUser);
            List<Integer> listInt = list.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            return ApplicationMapper.toListApplicationDto(
                    applicationRepository.findByStatusApplicationAndUserIdIn(StatusApplication.SHIPPED, listInt, pageable));
        } else { //получение не оператором список своих заявок
            List<Application> result = applicationRepository.findAllByUserId(idUser, pageable);
            if (!result.isEmpty()) {
                return ApplicationMapper.toListApplicationDto(result);
            } else {
                throw new ValidationException("У вас нет прав доступа или созданных заявок");
            }
        }
    }

    public List<ApplicationDto> getAllOperator(int from, int size, Boolean asc) { //получение оператором списка всех
        Pageable pageable = pageableOf(from, size, asc);
        List<Application> applications = applicationRepository.findByStatusApplication(StatusApplication.SHIPPED, pageable);
        applications.forEach(application -> {
            String description = application.getDescription();
            String formattedDescription = description.chars()
                    .mapToObj(c -> (char) c + "-")
                    .collect(Collectors.joining());
            application.setDescription(formattedDescription);
        });
        return ApplicationMapper.toListApplicationDto(applications);
    }

    public Boolean checkOperator(int idUser) {
        User user = userService.getUser(idUser);
        Set<Integer> list = user.getStatusId();
        return list.contains(1);
    }

    public ApplicationDto updateStatusOrDescription(ApplicationUpdateDto applicationDto, int idUser, int idApplication) {
        Application application = getApplication(idApplication);
        //если это автор заявки и она не отправлена, можно изменить или отправить
        if ((application.getStatusApplication().equals(StatusApplication.DRAFT)) && application.getUser().getId() == idUser) {
            return updateStatusAndDescriptionForCustomer(application, applicationDto);
        }
        if ((application.getStatusApplication().equals(StatusApplication.SHIPPED)) && checkOperator(idUser)) {
            return updateStatusForOperator(application, applicationDto);
        }
        throw new ValidationException("Вы не можете изменить заявку");
    }

    public ApplicationDto updateStatusAndDescriptionForCustomer(Application application, ApplicationUpdateDto applicationDto) {
        if (applicationDto.getDescription() != null) {
            application.setDescription(application.getDescription());
        }
        if (applicationDto.getStatusApplication() != null && (applicationDto.getStatusApplication().equals(StatusApplication.DRAFT)
                || applicationDto.getStatusApplication().equals(StatusApplication.SHIPPED))) {
            application.setStatusApplication(applicationDto.getStatusApplication());
            Application applicationSave = applicationRepository.save(application);
            return ApplicationMapper.toApplicationDto(applicationSave);
        } else {
            throw new ValidationException("Вы не можете принять или отклонить свою заявку");
        }
    }

    public ApplicationDto updateStatusForOperator(Application application, ApplicationUpdateDto applicationDto) {
        if (applicationDto.getStatusApplication() != null && (applicationDto.getStatusApplication().equals(StatusApplication.ACCEPTED)
                || applicationDto.getStatusApplication().equals(StatusApplication.REJECTED))) {
            application.setStatusApplication(applicationDto.getStatusApplication());
            Application applicationSave = applicationRepository.save(application);
            return ApplicationMapper.toApplicationDto(applicationSave);
        } else {
            throw new ValidationException("Вы можете только принять или отклонить заявку");
        }
    }

    public Application getApplication(int id) {
        return applicationRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Заявки не существует"));
    }

    public Pageable pageableOf(int from, int size, Boolean asc) {
        int page = from / size;
        if (asc) {
            return PageRequest.of(page, size, timeAsc);
        } else {
            return PageRequest.of(page, size, timeDesc);
        }
    }
}
