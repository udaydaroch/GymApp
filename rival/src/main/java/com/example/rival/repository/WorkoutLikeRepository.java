package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.WorkoutLike;
import com.example.rival.model.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutLikeRepository extends JpaRepository<WorkoutLike, Long> {

    Optional<WorkoutLike> findByUserAndWorkoutLog(AppUser user, WorkoutLog workoutLog);

    boolean existsByUserAndWorkoutLog(AppUser user, WorkoutLog workoutLog);

    long countByWorkoutLog(WorkoutLog workoutLog);
}
