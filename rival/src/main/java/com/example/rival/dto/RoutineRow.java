package com.example.rival.dto;

// One row of the routine form: an exercise with a number of sets and reps
public class RoutineRow {

    private Long exerciseId;
    private Integer sets = 3;
    private Integer reps = 10;

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
}
