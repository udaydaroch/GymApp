package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.WorkoutLog;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    List<WorkoutLog> findByUserOrderByWorkoutDateDescIdDesc(AppUser user);

    List<WorkoutLog> findByUserAndWorkoutDateBetween(AppUser user, LocalDate from, LocalDate to);

    long countByUser(AppUser user);

    // The feed: newest posted workouts from everyone
    List<WorkoutLog> findTop50ByPostedTrueOrderByWorkoutDateDescIdDesc();

    // The feed, limited to workouts by the given users
    List<WorkoutLog> findTop50ByPostedTrueAndUserInOrderByWorkoutDateDescIdDesc(Collection<AppUser> users);
}
