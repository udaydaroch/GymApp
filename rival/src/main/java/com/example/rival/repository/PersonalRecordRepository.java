package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.Exercise;
import com.example.rival.model.PersonalRecord;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {

    Optional<PersonalRecord> findByUserAndExercise(AppUser user, Exercise exercise);

    List<PersonalRecord> findByUserOrderByAchievedOnDescIdDesc(AppUser user);
}
