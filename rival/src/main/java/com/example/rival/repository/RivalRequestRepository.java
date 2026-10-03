package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.RequestStatus;
import com.example.rival.model.RivalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RivalRequestRepository extends JpaRepository<RivalRequest, Long> {

    List<RivalRequest> findByToUserAndStatusOrderByIdDesc(AppUser toUser, RequestStatus status);

    List<RivalRequest> findByFromUserAndStatusOrderByIdDesc(AppUser fromUser, RequestStatus status);

    boolean existsByFromUserAndToUserAndStatus(AppUser fromUser, AppUser toUser, RequestStatus status);
}
