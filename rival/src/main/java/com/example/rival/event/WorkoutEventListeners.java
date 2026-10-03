package com.example.rival.event;

import com.example.rival.model.BadgeType;
import com.example.rival.model.WorkoutLog;
import com.example.rival.service.ProgressService;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// OBSERVER PATTERN: WorkoutService only announces "a workout was logged".
// Everything that should happen as a result lives here, so new reactions can be
// added without touching the logging code. @Order sets the order they run in.
@Component
public class WorkoutEventListeners {

    private final ProgressService progressService;

    public WorkoutEventListeners(ProgressService progressService) {
        this.progressService = progressService;
    }

    @EventListener
    @Order(1)
    public void updateStreak(WorkoutLoggedEvent event) {
        WorkoutLog log = event.getWorkoutLog();
        progressService.updateStreak(log.getUser(), log.getWorkoutDate());
    }

    @EventListener
    @Order(2)
    public void checkPersonalRecords(WorkoutLoggedEvent event) {
        WorkoutLog log = event.getWorkoutLog();
        int beaten = progressService.updateRecords(log);
        if (beaten > 0) {
            progressService.award(log.getUser(), BadgeType.FIRST_PR);
        }
    }

    @EventListener
    @Order(3)
    public void awardBadges(WorkoutLoggedEvent event) {
        progressService.checkBadges(event.getWorkoutLog().getUser());
    }
}
