package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.StatusApplication;
import org.example.model.StatusUser;
import org.example.dto.ApplicationDto;
import org.example.dto.ApplicationMapper;
import org.example.dto.ApplicationUpdateDto;
import org.example.exception.EntityNotFoundException;
import org.example.exception.ValidationException;
import org.example.model.Application;
import org.example.model.User;
import org.example.repository.ApplicationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final UserService userService;
    private final ApplicationRepository applicationRepository;

    private Sort timeAsc = Sort.by(Sort.Direction.ASC, "timeCreate"); //по возрастанию
    private Sort timeDesc = Sort.by(Sort.Direction.DESC, "timeCreate"); //по убыванию

    public ApplicationDto create(ApplicationDto applicationDto, int idUser) {
        User user = userService.getUser(idUser); //проверяем что такой пользователь существует
        if (!user.getStatus().contains(StatusUser.CUSTOMER)) {
            throw new ValidationException("Только пользователи могут создавать заявки");
        }
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
        int page = from / size;
        Pageable pageable;
        if (checkOperator(idUser)) {
            if (search == null || search.isBlank()) {
                return getAll(from, size, asc);
            } //получение оператором списка заявок по части имени
            List<User> list = userService.searchUser(search, idUser);
            List<Integer> listInt = list.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            if (asc) {
                pageable = PageRequest.of(page, size, timeAsc);
            } else {
                pageable = PageRequest.of(page, size, timeDesc);
            }
            return ApplicationMapper.toListApplicationDto(
                    applicationRepository.findByStatusApplicationAndUserIdIn(StatusApplication.SHIPPED, listInt, pageable));
        } else { //получение не оператором список своих заявок
            if (asc) {
                pageable = PageRequest.of(page, size, timeAsc);
            } else {
                pageable = PageRequest.of(page, size, timeDesc);
            }
            List<Application> result = applicationRepository.findAllByUserId(idUser, pageable);
            if (!result.isEmpty()) {
                return ApplicationMapper.toListApplicationDto(result);
            } else {
                throw new ValidationException("У вас нет прав доступа или созданных заявок");
            }
        }
    }

    public List<ApplicationDto> getAll(int from, int size, Boolean asc) { //получение оператором списка всех
        int page = from / size;
        if (asc) {
            Pageable pageable = PageRequest.of(page, size, timeAsc);
            return ApplicationMapper.toListApplicationDto(
                    applicationRepository.findByStatusApplication(StatusApplication.SHIPPED, pageable));
        } else {
            Pageable pageable = PageRequest.of(page, size, timeDesc);
            return ApplicationMapper.toListApplicationDto(
                    applicationRepository.findByStatusApplication(StatusApplication.SHIPPED, pageable));
        }
    }

    public Boolean checkOperator(int idUser) {
        User user = userService.getUser(idUser);
        List<StatusUser> list = user.getStatus();
        if (list.contains(StatusUser.OPERATOR)) {
            return true;
        }
        return false;
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
}
