package com.example.rival.service;

import com.example.rival.dto.ClientSummary;
import com.example.rival.model.AppUser;
import com.example.rival.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainerService {

    private final UserRepository userRepository;
    private final WorkoutService workoutService;

    public TrainerService(UserRepository userRepository, WorkoutService workoutService) {
        this.userRepository = userRepository;
        this.workoutService = workoutService;
    }

    // One summary per client, with an early warning for anyone drifting away
    public List<ClientSummary> clientSummaries(AppUser trainer) {
        List<ClientSummary> summaries = new ArrayList<>();
        for (AppUser client : userRepository.findByTrainerOrderByDisplayNameAsc(trainer)) {
            Long daysSince = client.getLastWorkoutDate() == null
                ? null
                : ChronoUnit.DAYS.between(client.getLastWorkoutDate(), LocalDate.now());

            String status;
            if (daysSince == null || daysSince >= 7) {
                status = "At risk";
            } else if (daysSince >= 4) {
                status = "Slipping";
            } else {
                status = "On track";
            }
            summaries.add(new ClientSummary(
                client, workoutService.thisWeek(client).size(), daysSince, status));
        }
        return summaries;
    }
}
