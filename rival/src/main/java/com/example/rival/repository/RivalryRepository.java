package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.Rivalry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RivalryRepository extends JpaRepository<Rivalry, Long> {

    // The user can be on either side of the rivalry
    @Query("select r from Rivalry r where r.active = true and (r.userA = :user or r.userB = :user)")
    Optional<Rivalry> findActiveFor(@Param("user") AppUser user);

    List<Rivalry> findByActiveTrue();
}
