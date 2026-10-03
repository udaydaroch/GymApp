package com.example.rival.dto;

// One row of the workout form: an exercise, reps and weight
public class SetRow {

    private Long exerciseId;
    private Integer reps;
    private Double weight;

    public SetRow() {
    }

    public SetRow(Long exerciseId, Integer reps, Double weight) {
        this.exerciseId = exerciseId;
        this.reps = reps;
        this.weight = weight;
    }

    // Blank rows on the form are simply ignored
    public boolean isFilledIn() {
        return exerciseId != null && reps != null && reps > 0;
    }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
}
