package com.example.rival.repository;

import com.example.rival.model.Rivalry;
import com.example.rival.model.WeeklyBattle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyBattleRepository extends JpaRepository<WeeklyBattle, Long> {

    Optional<WeeklyBattle> findByRivalryAndWeekStart(Rivalry rivalry, LocalDate weekStart);

    List<WeeklyBattle> findByClosedFalse();

    List<WeeklyBattle> findByRivalryAndClosedTrueOrderByWeekStartDesc(Rivalry rivalry);
}
