package com.example.rival.builder;

import com.example.rival.dto.SetRow;
import com.example.rival.dto.WorkoutForm;
import com.example.rival.model.Exercise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// BUILDER PATTERN: collects the constraints one at a time, then build() turns them
// into a ready-to-log workout.
public class WorkoutPlanBuilder {

    private static final int MINUTES_PER_EXERCISE = 8;
    private static final int MAX_EXERCISES = 6;

    private final List<Exercise> pool;
    private int minutes = 15;
    private int energy = 3;
    private final Set<String> equipment = new HashSet<>();
    private String sore;
    private Map<Long, Double> bestWeights = new HashMap<>();

    private WorkoutPlanBuilder(List<Exercise> pool) {
        this.pool = pool;
        this.equipment.add("None"); // bodyweight is always available
    }

    public static WorkoutPlanBuilder from(List<Exercise> allExercises) {
        return new WorkoutPlanBuilder(allExercises);
    }

    public WorkoutPlanBuilder minutes(int minutes) {
        this.minutes = minutes;
        return this;
    }

    public WorkoutPlanBuilder energy(int energy) {
        this.energy = energy;
        return this;
    }

    public WorkoutPlanBuilder equipment(Collection<String> available) {
        if (available != null) {
            this.equipment.addAll(available);
        }
        return this;
    }

    public WorkoutPlanBuilder avoiding(String muscleGroup) {
        this.sore = muscleGroup;
        return this;
    }

    // Exercise id -> the user's best weight. Known exercises are preferred.
    public WorkoutPlanBuilder knownWeights(Map<Long, Double> bestWeights) {
        this.bestWeights = bestWeights;
        return this;
    }

    public WorkoutForm build() {
        List<Exercise> chosen = chooseExercises();

        int sets = energy >= 4 ? 3 : energy >= 2 ? 2 : 1;
        int reps = energy >= 3 ? 10 : 8;
        double effort = energy >= 4 ? 1.0 : energy == 3 ? 0.9 : 0.8;

        WorkoutForm form = new WorkoutForm();
        form.setDurationMinutes(minutes);
        form.setEnergy(energy);
        for (Exercise exercise : chosen) {
            Double best = bestWeights.get(exercise.getId());
            // Round the suggested weight to the nearest 0.5 kg
            double weight = best == null ? 0 : Math.round(best * effort * 2) / 2.0;
            for (int i = 0; i < sets; i++) {
                form.getRows().add(new SetRow(exercise.getId(), reps, weight));
            }
        }
        return form;
    }

    private List<Exercise> chooseExercises() {
        int wanted = Math.max(1, Math.min(MAX_EXERCISES, minutes / MINUTES_PER_EXERCISE));

        // 1. Keep only exercises the user can do right now
        List<Exercise> familiar = new ArrayList<>();
        List<Exercise> unfamiliar = new ArrayList<>();
        for (Exercise exercise : pool) {
            boolean hasEquipment = equipment.contains(exercise.getEquipment());
            boolean isSore = sore != null && sore.equalsIgnoreCase(exercise.getMuscleGroup());
            if (hasEquipment && !isSore) {
                if (bestWeights.containsKey(exercise.getId())) {
                    familiar.add(exercise);
                } else {
                    unfamiliar.add(exercise);
                }
            }
        }
        // 2. Exercises the user already knows come first
        List<Exercise> candidates = new ArrayList<>(familiar);
        candidates.addAll(unfamiliar);

        // 3. First pass: one exercise per muscle group, for a balanced session
        List<Exercise> chosen = new ArrayList<>();
        Set<String> usedGroups = new HashSet<>();
        for (Exercise exercise : candidates) {
            if (chosen.size() < wanted && usedGroups.add(exercise.getMuscleGroup())) {
                chosen.add(exercise);
            }
        }
        // 4. Second pass: fill any remaining slots
        for (Exercise exercise : candidates) {
            if (chosen.size() < wanted && !chosen.contains(exercise)) {
                chosen.add(exercise);
            }
        }
        return chosen;
    }
}
