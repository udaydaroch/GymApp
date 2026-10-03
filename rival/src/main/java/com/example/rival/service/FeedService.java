package com.example.rival.service;

import com.example.rival.dto.FeedItem;
import com.example.rival.model.AppUser;
import com.example.rival.model.SetEntry;
import com.example.rival.model.WorkoutLike;
import com.example.rival.model.WorkoutLog;
import com.example.rival.repository.WorkoutLikeRepository;
import com.example.rival.repository.WorkoutLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// The shared feed of posted workouts, and likes on them
@Service
public class FeedService {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutLikeRepository likeRepository;
    private final FollowService followService;

    public FeedService(WorkoutLogRepository workoutLogRepository, WorkoutLikeRepository likeRepository,
                       FollowService followService) {
        this.workoutLogRepository = workoutLogRepository;
        this.likeRepository = likeRepository;
        this.followService = followService;
    }

    // The 50 newest posts: from everyone, or only from people the viewer follows
    public List<FeedItem> feed(AppUser viewer, boolean followingOnly) {
        List<AppUser> following = followService.following(viewer);
        Set<Long> followingIds = followService.followingIds(viewer);

        List<WorkoutLog> logs;
        if (!followingOnly) {
            logs = workoutLogRepository.findTop50ByPostedTrueOrderByWorkoutDateDescIdDesc();
        } else if (following.isEmpty()) {
            logs = List.of();
        } else {
            logs = workoutLogRepository.findTop50ByPostedTrueAndUserInOrderByWorkoutDateDescIdDesc(following);
        }

        List<FeedItem> items = new ArrayList<>();
        for (WorkoutLog log : logs) {
            Long authorId = log.getUser().getId();
            items.add(new FeedItem(
                log,
                exerciseSummary(log),
                likeRepository.countByWorkoutLog(log),
                likeRepository.existsByUserAndWorkoutLog(viewer, log),
                authorId.equals(viewer.getId()),
                followingIds.contains(authorId)));
        }
        return items;
    }

    // Likes the post, or takes the like back if the user had already liked it
    @Transactional
    public void toggleLike(AppUser user, Long workoutLogId) {
        WorkoutLog log = workoutLogRepository.findById(workoutLogId)
            .filter(WorkoutLog::isPosted)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Optional<WorkoutLike> existing = likeRepository.findByUserAndWorkoutLog(user, log);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
        } else {
            likeRepository.save(new WorkoutLike(user, log));
        }
    }

    // Each exercise name once, in the order it was done
    private String exerciseSummary(WorkoutLog log) {
        Set<String> names = new LinkedHashSet<>();
        for (SetEntry set : log.getSets()) {
            names.add(set.getExercise().getName());
        }
        return String.join(", ", names);
    }
}
