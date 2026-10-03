package com.example.rival.repository;

import com.example.rival.model.Exercise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findAllByOrderByMuscleGroupAscNameAsc();
}
