package com.example.rival.service;

import com.example.rival.model.AppUser;
import com.example.rival.model.Badge;
import com.example.rival.model.BadgeType;
import com.example.rival.model.PersonalRecord;
import com.example.rival.model.SetEntry;
import com.example.rival.model.WorkoutLog;
import com.example.rival.repository.BadgeRepository;
import com.example.rival.repository.PersonalRecordRepository;
import com.example.rival.repository.UserRepository;
import com.example.rival.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Streaks, personal records and badges
@Service
public class ProgressService {

    private final UserRepository userRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final PersonalRecordRepository recordRepository;
    private final BadgeRepository badgeRepository;

    public ProgressService(UserRepository userRepository,
                           WorkoutLogRepository workoutLogRepository,
                           PersonalRecordRepository recordRepository,
                           BadgeRepository badgeRepository) {
        this.userRepository = userRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.recordRepository = recordRepository;
        this.badgeRepository = badgeRepository;
    }

    @Transactional
    public void updateStreak(AppUser user, LocalDate workoutDate) {
        LocalDate last = user.getLastWorkoutDate();

        if (last != null && !workoutDate.isAfter(last)) {
            return; // a second workout on the same day changes nothing
        }
        if (last != null && workoutDate.equals(last.plusDays(1))) {
            user.setCurrentStreak(user.getCurrentStreak() + 1); // trained yesterday too
        } else {
            user.setCurrentStreak(1); // first workout, or a day was missed
        }
        user.setLastWorkoutDate(workoutDate);
        user.setLongestStreak(Math.max(user.getLongestStreak(), user.getCurrentStreak()));
        userRepository.save(user);
    }

    // Returns how many existing records this workout beat
    @Transactional
    public int updateRecords(WorkoutLog log) {
        int beaten = 0;
        for (SetEntry set : log.getSets()) {
            if (set.getWeight() <= 0) {
                continue; // bodyweight sets have no weight record
            }
            Optional<PersonalRecord> existing =
                recordRepository.findByUserAndExercise(log.getUser(), set.getExercise());

            if (existing.isEmpty()) {
                // First time doing this exercise: store a starting point
                recordRepository.save(new PersonalRecord(
                    log.getUser(), set.getExercise(), set.getWeight(), log.getWorkoutDate()));
            } else if (set.getWeight() > existing.get().getWeight()) {
                PersonalRecord record = existing.get();
                record.setWeight(set.getWeight());
                record.setAchievedOn(log.getWorkoutDate());
                recordRepository.save(record);
                beaten++;
            }
        }
        return beaten;
    }

    @Transactional
    public void checkBadges(AppUser user) {
        long workouts = workoutLogRepository.countByUser(user);
        if (workouts >= 1) {
            award(user, BadgeType.FIRST_WORKOUT);
        }
        if (workouts >= 10) {
            award(user, BadgeType.TEN_WORKOUTS);
        }
        if (user.getLongestStreak() >= 3) {
            award(user, BadgeType.STREAK_3);
        }
        if (user.getLongestStreak() >= 7) {
            award(user, BadgeType.STREAK_7);
        }
    }

    // Each badge can only be earned once
    @Transactional
    public void award(AppUser user, BadgeType type) {
        if (!badgeRepository.existsByUserAndType(user, type)) {
            badgeRepository.save(new Badge(user, type, LocalDate.now()));
        }
    }

    public List<Badge> badgesFor(AppUser user) {
        return badgeRepository.findByUserOrderByEarnedOnDescIdDesc(user);
    }

    public List<PersonalRecord> recordsFor(AppUser user) {
        return recordRepository.findByUserOrderByAchievedOnDescIdDesc(user);
    }

    // Exercise id -> best weight, used by the routine and "No Excuses" screens
    public Map<Long, Double> bestWeights(AppUser user) {
        Map<Long, Double> result = new HashMap<>();
        for (PersonalRecord record : recordsFor(user)) {
            result.put(record.getExercise().getId(), record.getWeight());
        }
        return result;
    }
}
