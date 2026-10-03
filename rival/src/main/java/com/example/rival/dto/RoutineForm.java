package com.example.rival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class RoutineForm {

    // The most exercises one routine can have
    public static final int MAX_ROWS = 30;

    @NotBlank(message = "Name is required")
    @Size(max = 60, message = "Name must be 60 characters or fewer")
    private String name;

    @Size(max = 255, message = "Description must be 255 characters or fewer")
    private String description;

    private boolean shared;

    private List<RoutineRow> rows = new ArrayList<>();

    public static RoutineForm withBlankRows(int count) {
        RoutineForm form = new RoutineForm();
        form.addBlankRows(count);
        return form;
    }

    // Adds blank rows, stopping at MAX_ROWS
    public void addBlankRows(int count) {
        for (int i = 0; i < count && rows.size() < MAX_ROWS; i++) {
            rows.add(new RoutineRow());
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

    public List<RoutineRow> filledInRows() {
        List<RoutineRow> result = new ArrayList<>();
        for (RoutineRow row : rows) {
            if (row.getExerciseId() != null) {
                result.add(row);
            }
        }
        return result;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isShared() { return shared; }
    public void setShared(boolean shared) { this.shared = shared; }

    public List<RoutineRow> getRows() { return rows; }
    public void setRows(List<RoutineRow> rows) { this.rows = rows; }
}
