package com.example.rival.model;

public enum BadgeType {
    FIRST_WORKOUT("First Rep", "Logged your first workout"),
    STREAK_3("On a Roll", "Trained 3 days in a row"),
    STREAK_7("Unstoppable", "Trained 7 days in a row"),
    TEN_WORKOUTS("Regular", "Logged 10 workouts"),
    FIRST_PR("New Best", "Beat a personal record"),
    BATTLE_WON("Victor", "Won a weekly battle");

    private final String title;
    private final String description;

    BadgeType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
