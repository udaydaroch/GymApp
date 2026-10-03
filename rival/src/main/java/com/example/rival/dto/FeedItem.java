package com.example.rival.dto;

import com.example.rival.model.WorkoutLog;

// One post on the feed, as seen by the person looking at it
public class FeedItem {

    private final WorkoutLog log;
    private final String exerciseSummary; // "Squat, Bench Press, Bent-over Row"
    private final long likeCount;
    private final boolean likedByMe;
    private final boolean mine;
    private final boolean followingAuthor;

    public FeedItem(WorkoutLog log, String exerciseSummary, long likeCount,
                    boolean likedByMe, boolean mine, boolean followingAuthor) {
        this.log = log;
        this.exerciseSummary = exerciseSummary;
        this.likeCount = likeCount;
        this.likedByMe = likedByMe;
        this.mine = mine;
        this.followingAuthor = followingAuthor;
    }

    public WorkoutLog getLog() { return log; }
    public String getExerciseSummary() { return exerciseSummary; }
    public long getLikeCount() { return likeCount; }
    public boolean isLikedByMe() { return likedByMe; }
    public boolean isMine() { return mine; }
    public boolean isFollowingAuthor() { return followingAuthor; }
}
