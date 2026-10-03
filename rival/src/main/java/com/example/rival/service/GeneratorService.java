package com.example.rival.service;

import com.example.rival.builder.WorkoutPlanBuilder;
import com.example.rival.dto.GeneratorForm;
import com.example.rival.dto.WorkoutForm;
import com.example.rival.model.AppUser;
import com.example.rival.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

// "No Excuses": builds the smallest sensible workout for how the user feels right now
@Service
public class GeneratorService {

    private final ExerciseRepository exerciseRepository;
    private final ProgressService progressService;

    public GeneratorService(ExerciseRepository exerciseRepository, ProgressService progressService) {
        this.exerciseRepository = exerciseRepository;
        this.progressService = progressService;
    }

    public WorkoutForm generate(AppUser user, GeneratorForm form) {
        String sore = form.getSore() == null || form.getSore().isBlank() ? null : form.getSore();

        return WorkoutPlanBuilder.from(exerciseRepository.findAllByOrderByMuscleGroupAscNameAsc())
            .minutes(form.getMinutes())
            .energy(form.getEnergy())
            .equipment(form.getEquipment())
            .avoiding(sore)
            .knownWeights(progressService.bestWeights(user))
            .build();
    }
}
