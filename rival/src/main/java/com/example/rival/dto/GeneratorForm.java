package com.example.rival.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

// The "No Excuses" questions: how much time and energy you have right now
public class GeneratorForm {

    @NotNull(message = "Tell us how many minutes you have")
    @Min(value = 5, message = "Give it at least 5 minutes")
    @Max(value = 120, message = "120 minutes is the maximum")
    private Integer minutes = 15;

    @NotNull(message = "Energy is required")
    @Min(value = 1, message = "Energy is 1 to 5")
    @Max(value = 5, message = "Energy is 1 to 5")
    private Integer energy = 3;

    // Any of: Dumbbells, Barbell, Machine. Bodyweight exercises are always allowed.
    private List<String> equipment = new ArrayList<>();

    // A muscle group to leave out, or empty
    private String sore;

    public Integer getMinutes() { return minutes; }
    public void setMinutes(Integer minutes) { this.minutes = minutes; }

    public Integer getEnergy() { return energy; }
    public void setEnergy(Integer energy) { this.energy = energy; }

    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }

    public String getSore() { return sore; }
    public void setSore(String sore) { this.sore = sore; }
}
