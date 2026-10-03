package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    List<AppUser> findByRoleOrderByDisplayNameAsc(Role role);

    List<AppUser> findByTrainerOrderByDisplayNameAsc(AppUser trainer);

    // Everyone except users with the given role
    List<AppUser> findByRoleNotOrderByDisplayNameAsc(Role role);
}
