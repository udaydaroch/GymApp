package com.example.rival.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WorkoutForm {

    // The most set rows one workout can have
    public static final int MAX_ROWS = 30;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 300, message = "Duration must be 300 minutes or less")
    private Integer durationMinutes = 30;

    @NotNull(message = "Energy is required")
    @Min(value = 1, message = "Energy is 1 to 5")
    @Max(value = 5, message = "Energy is 1 to 5")
    private Integer energy = 3;

    // Post this workout to the shared feed (ticked by default)
    private boolean posted = true;

    private List<SetRow> rows = new ArrayList<>();

    public static WorkoutForm withBlankRows(int count) {
        WorkoutForm form = new WorkoutForm();
        form.addBlankRows(count);
        return form;
    }

    // Adds blank rows, stopping at MAX_ROWS
    public void addBlankRows(int count) {
        for (int i = 0; i < count && rows.size() < MAX_ROWS; i++) {
            rows.add(new SetRow());
        }
    }

    // Removes the row at this position. The last remaining row is cleared instead of removed.
    public void removeRow(int index) {
        if (index < 0 || index >= rows.size()) {
            return;
        }
        rows.remove(index);
        if (rows.isEmpty()) {
            addBlankRows(1);
        }
    }

    public boolean isFull() {
        return rows.size() >= MAX_ROWS;
    }

    public List<SetRow> filledInRows() {
        List<SetRow> result = new ArrayList<>();
        for (SetRow row : rows) {
            if (row.isFilledIn()) {
                result.add(row);
            }
        }
        return result;
    }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getEnergy() { return energy; }
    public void setEnergy(Integer energy) { this.energy = energy; }

    public boolean isPosted() { return posted; }
    public void setPosted(boolean posted) { this.posted = posted; }

    public List<SetRow> getRows() { return rows; }
    public void setRows(List<SetRow> rows) { this.rows = rows; }
}
