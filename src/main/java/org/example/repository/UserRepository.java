package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(" select u from User u " +
            "where upper(u.name) like upper(concat('%', ?1, '%')) ")
    List<User> search(String text);
}
