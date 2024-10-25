package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityNotFoundException;
import org.example.model.User;
import org.example.repository.StatusRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;

    @Transactional
    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователя не существует"));
    }

    public List<User> searchUser(String textQuery, int idUser) {
        if (textQuery == null || textQuery.isBlank()) {
            return Collections.emptyList();
        }
        return userRepository.search(textQuery);
    }

    public List<User> getAll(int idUser) {
        return userRepository.findAll();
    }

    public User createOperator(int idUser, int idCustomer) {
        User user = getUser(idCustomer);
        user.getStatus().add(statusRepository.getById(1));
        return userRepository.save(user);
    }

}
