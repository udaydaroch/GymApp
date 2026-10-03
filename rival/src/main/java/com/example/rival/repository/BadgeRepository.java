package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.Badge;
import com.example.rival.model.BadgeType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    boolean existsByUserAndType(AppUser user, BadgeType type);

    List<Badge> findByUserOrderByEarnedOnDescIdDesc(AppUser user);
}
