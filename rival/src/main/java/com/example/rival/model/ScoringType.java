package com.example.rival.model;

public enum ScoringType {
    IMPROVEMENT("Improvement", "This week's volume as a percentage of your own usual week. Beginners can beat veterans."),
    CONSISTENCY("Consistency", "10 points for every different day you train this week."),
    VOLUME("Volume", "Total weight moved this week (reps x kg), divided by 100.");

    private final String label;
    private final String explanation;

    ScoringType(String label, String explanation) {
        this.label = label;
        this.explanation = explanation;
    }

    public String getLabel() { return label; }
    public String getExplanation() { return explanation; }
}
