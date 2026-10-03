package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.Routine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByOwnerOrderByIdDesc(AppUser owner);

    // Shared routines that belong to other people, most copied first
    List<Routine> findBySharedTrueAndOwnerNotOrderByTimesCopiedDescIdDesc(AppUser owner);

    List<Routine> findByCopiedFrom(Routine copiedFrom);
}
