package com.example.rival.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Legs, Chest, Back, Shoulders, Arms, Core
    @Column(nullable = false)
    private String muscleGroup;

    // None, Dumbbells, Barbell, Machine
    @Column(nullable = false)
    private String equipment;

    public Exercise() {
    }

    public Exercise(String name, String muscleGroup, String equipment) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
}
