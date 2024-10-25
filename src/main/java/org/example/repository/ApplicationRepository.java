package org.example.repository;

import org.example.model.Application;
import org.example.model.StatusApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    List<Application> findByStatusApplicationAndUserIdIn(StatusApplication statusApplication, List<Integer> userId, Pageable pageable);

    List<Application> findByStatusApplication(StatusApplication statusApplication, Pageable pageable);

    List<Application> findAllByUserId(Integer idUser, Pageable pageable);
}
