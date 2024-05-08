package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.StatusUser;
import org.example.exception.EntityNotFoundException;
import org.example.exception.ValidationException;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователя не существует"));
    }

    public List<User> searchUser(String textQuery, int idUser) {
        User user = getUser(idUser);
        List<StatusUser> list = user.getStatus();
        if (list.contains(StatusUser.ADMINISTRATOR) || list.contains(StatusUser.OPERATOR)) {
            if (textQuery == null || textQuery.isBlank()) {
                return Collections.emptyList();
            }
            return userRepository.search(textQuery);
        }
        throw new ValidationException("Вы не обладаете нужными правами для поиска");
    }

    public List<User> getAll(int idUser) {
        User user = getUser(idUser);
        List<StatusUser> list = user.getStatus();
        if (list.contains(StatusUser.ADMINISTRATOR) || list.contains(StatusUser.OPERATOR)) {
            return userRepository.findAll();
        }
        throw new ValidationException("Вы не обладаете нужными правами для поиска");
    }

    public User createOperator(int idUser, int idCustomer) {
        if (checkAdmin(idUser)) {
            User user = getUser(idCustomer);
            user.getStatus().add(StatusUser.OPERATOR);
            return userRepository.save(user);
        }
        throw new ValidationException("Вы не обладаете нужными правами для добавления статуса");
    }

    public Boolean checkAdmin(int idUser) {
        User user = getUser(idUser);
        List<StatusUser> list = user.getStatus();
        if (list.contains(StatusUser.ADMINISTRATOR)) {
            return true;
        }
        return false;
    }
}
