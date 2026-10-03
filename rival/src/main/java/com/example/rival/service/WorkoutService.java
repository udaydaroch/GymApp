package com.example.rival.service;

import com.example.rival.dto.SetRow;
import com.example.rival.dto.WorkoutForm;
import com.example.rival.event.WorkoutLoggedEvent;
import com.example.rival.model.AppUser;
import com.example.rival.model.Exercise;
import com.example.rival.model.SetEntry;
import com.example.rival.model.WorkoutLog;
import com.example.rival.repository.ExerciseRepository;
import com.example.rival.repository.WorkoutLogRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseRepository exerciseRepository;
    private final ApplicationEventPublisher events;

    public WorkoutService(WorkoutLogRepository workoutLogRepository,
                          ExerciseRepository exerciseRepository,
                          ApplicationEventPublisher events) {
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseRepository = exerciseRepository;
        this.events = events;
    }

    // The Monday of the week that contains the given date
    public static LocalDate weekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public List<Exercise> allExercises() {
        return exerciseRepository.findAllByOrderByMuscleGroupAscNameAsc();
    }

    @Transactional
    public WorkoutLog logWorkout(AppUser user, WorkoutForm form) {
        WorkoutLog log = new WorkoutLog(user, LocalDate.now(), form.getDurationMinutes(), form.getEnergy());
        log.setPosted(form.isPosted());

        for (SetRow row : form.filledInRows()) {
            Exercise exercise = exerciseRepository.findById(row.getExerciseId()).orElse(null);
            if (exercise == null) {
                continue;
            }
            int reps = Math.min(row.getReps(), 200);
            double weight = row.getWeight() == null ? 0 : Math.max(0, Math.min(row.getWeight(), 1000));
            log.addSet(new SetEntry(exercise, reps, weight));
        }

        WorkoutLog saved = workoutLogRepository.save(log);

        // Tell the rest of the app. Streaks, records and badges react to this
        // in WorkoutEventListeners, inside the same database transaction.
        events.publishEvent(new WorkoutLoggedEvent(saved));
        return saved;
    }

    public List<WorkoutLog> history(AppUser user) {
        return workoutLogRepository.findByUserOrderByWorkoutDateDescIdDesc(user);
    }

    public List<WorkoutLog> thisWeek(AppUser user) {
        LocalDate monday = weekStart(LocalDate.now());
        return workoutLogRepository.findByUserAndWorkoutDateBetween(user, monday, monday.plusDays(6));
    }

    public long volumeOf(List<WorkoutLog> logs) {
        long total = 0;
        for (WorkoutLog log : logs) {
            total += log.getTotalVolume();
        }
        return total;
    }
}
