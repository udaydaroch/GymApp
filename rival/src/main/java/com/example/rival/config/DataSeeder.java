package com.example.rival.config;

import com.example.rival.model.AppUser;
import com.example.rival.model.BadgeType;
import com.example.rival.model.Exercise;
import com.example.rival.model.Follow;
import com.example.rival.model.RivalRequest;
import com.example.rival.model.WorkoutLike;
import com.example.rival.model.Rivalry;
import com.example.rival.model.Role;
import com.example.rival.model.Routine;
import com.example.rival.model.RoutineExercise;
import com.example.rival.model.ScoringType;
import com.example.rival.model.SetEntry;
import com.example.rival.model.WeeklyBattle;
import com.example.rival.model.WorkoutLog;
import com.example.rival.repository.ExerciseRepository;
import com.example.rival.repository.FollowRepository;
import com.example.rival.repository.RivalRequestRepository;
import com.example.rival.repository.WorkoutLikeRepository;
import com.example.rival.repository.RivalryRepository;
import com.example.rival.repository.RoutineRepository;
import com.example.rival.repository.UserRepository;
import com.example.rival.repository.WeeklyBattleRepository;
import com.example.rival.repository.WorkoutLogRepository;
import com.example.rival.service.ProgressService;
import com.example.rival.service.RivalryService;
import com.example.rival.service.WorkoutService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

// Fills the empty in-memory database with demo data every time the app starts
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final ExerciseRepository exercises;
    private final WorkoutLogRepository workoutLogs;
    private final RoutineRepository routines;
    private final RivalryRepository rivalries;
    private final WeeklyBattleRepository battles;
    private final FollowRepository follows;
    private final WorkoutLikeRepository likes;
    private final RivalRequestRepository rivalRequests;
    private final ProgressService progressService;
    private final RivalryService rivalryService;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, ExerciseRepository exercises, WorkoutLogRepository workoutLogs,
                      RoutineRepository routines, RivalryRepository rivalries, WeeklyBattleRepository battles,
                      FollowRepository follows, WorkoutLikeRepository likes, RivalRequestRepository rivalRequests,
                      ProgressService progressService, RivalryService rivalryService, PasswordEncoder encoder) {
        this.users = users;
        this.exercises = exercises;
        this.workoutLogs = workoutLogs;
        this.routines = routines;
        this.rivalries = rivalries;
        this.battles = battles;
        this.follows = follows;
        this.likes = likes;
        this.rivalRequests = rivalRequests;
        this.progressService = progressService;
        this.rivalryService = rivalryService;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() > 0) {
            return;
        }

        // ---- Exercises ----
        Exercise squat = exercise("Squat", "Legs", "Barbell");
        Exercise bench = exercise("Bench Press", "Chest", "Barbell");
        Exercise row = exercise("Bent-over Row", "Back", "Barbell");
        Exercise deadlift = exercise("Deadlift", "Back", "Barbell");
        Exercise overhead = exercise("Overhead Press", "Shoulders", "Barbell");
        Exercise goblet = exercise("Goblet Squat", "Legs", "Dumbbells");
        Exercise dbPress = exercise("Dumbbell Press", "Chest", "Dumbbells");
        Exercise dbRow = exercise("Dumbbell Row", "Back", "Dumbbells");
        Exercise curl = exercise("Bicep Curl", "Arms", "Dumbbells");
        Exercise lateral = exercise("Lateral Raise", "Shoulders", "Dumbbells");
        Exercise legPress = exercise("Leg Press", "Legs", "Machine");
        Exercise pulldown = exercise("Lat Pulldown", "Back", "Machine");
        Exercise pushUp = exercise("Push-up", "Chest", "None");
        Exercise lunge = exercise("Bodyweight Lunge", "Legs", "None");
        Exercise plank = exercise("Plank (seconds as reps)", "Core", "None");
        Exercise crunch = exercise("Crunch", "Core", "None");
        Exercise dip = exercise("Bench Dip", "Arms", "None");
        Exercise pike = exercise("Pike Push-up", "Shoulders", "None");

        // ---- Users ----
        users.save(new AppUser("admin", encoder.encode("admin"), "Admin", Role.ADMIN));
        AppUser coach = users.save(new AppUser("coach", encoder.encode("password"), "Coach Mia", Role.TRAINER));
        AppUser alex = member("alex", "Alex", coach);
        AppUser sam = member("sam", "Sam", coach);
        AppUser jordan = member("jordan", "Jordan", coach);
        AppUser riley = member("riley", "Riley", coach); // has never trained

        // ---- Workout history (numbers are "days ago") ----
        Exercise[] barbellDay = {squat, bench, row};
        // Alex: regular, trained the last three days in a row
        seedHistory(alex, barbellDay, 1.0, 30, 27, 24, 20, 17, 15, 12, 10, 8, 5, 3, 2, 1);
        // Sam: regular, a little lighter, last trained two days ago
        seedHistory(sam, barbellDay, 0.9, 30, 25, 23, 18, 16, 11, 9, 4, 2);
        // Jordan: has drifted away, nothing for 12 days
        seedHistory(jordan, barbellDay, 0.7, 28, 26, 21, 19, 14, 12);

        // ---- Who follows whom ----
        follows.save(new Follow(alex, sam));
        follows.save(new Follow(sam, alex));
        follows.save(new Follow(alex, coach));
        follows.save(new Follow(sam, coach));
        follows.save(new Follow(coach, alex));
        follows.save(new Follow(jordan, alex));
        follows.save(new Follow(riley, jordan));

        // ---- Likes on the newest posts (every seeded workout is posted to the feed) ----
        WorkoutLog alexLatest = workoutLogs.findByUserOrderByWorkoutDateDescIdDesc(alex).get(0);
        WorkoutLog samLatest = workoutLogs.findByUserOrderByWorkoutDateDescIdDesc(sam).get(0);
        likes.save(new WorkoutLike(sam, alexLatest));
        likes.save(new WorkoutLike(coach, alexLatest));
        likes.save(new WorkoutLike(jordan, alexLatest));
        likes.save(new WorkoutLike(alex, samLatest));
        likes.save(new WorkoutLike(coach, samLatest));

        // ---- Riley has asked Jordan to be rivals; Jordan hasn't answered yet ----
        rivalRequests.save(new RivalRequest(riley, jordan, LocalDate.now()));

        // ---- Shared routines ----
        Routine fullBody = new Routine("Coach Mia's Full Body",
            "Three big lifts. Add weight when all sets feel easy.", coach, true);
        fullBody.addItem(new RoutineExercise(squat, 3, 8));
        fullBody.addItem(new RoutineExercise(bench, 3, 8));
        fullBody.addItem(new RoutineExercise(row, 3, 8));
        fullBody.addItem(new RoutineExercise(plank, 2, 45));
        fullBody.setTimesCopied(2);
        routines.save(fullBody);

        Routine hotel = new Routine("Hotel Room Circuit",
            "No equipment needed. Good for travel days.", sam, true);
        hotel.addItem(new RoutineExercise(pushUp, 3, 12));
        hotel.addItem(new RoutineExercise(lunge, 3, 12));
        hotel.addItem(new RoutineExercise(dip, 2, 10));
        hotel.addItem(new RoutineExercise(crunch, 2, 15));
        routines.save(hotel);

        Routine dumbbells = new Routine("Dumbbell Upper Body",
            "My private routine for busy gym days.", alex, false);
        dumbbells.addItem(new RoutineExercise(dbPress, 3, 10));
        dumbbells.addItem(new RoutineExercise(dbRow, 3, 10));
        dumbbells.addItem(new RoutineExercise(curl, 2, 12));
        routines.save(dumbbells);

        // ---- Alex vs Sam ----
        Rivalry rivalry = rivalries.save(new Rivalry(alex, sam, LocalDate.now().minusDays(14)));
        LocalDate lastMonday = WorkoutService.weekStart(LocalDate.now()).minusDays(7);
        battles.save(new WeeklyBattle(rivalry, lastMonday, ScoringType.IMPROVEMENT));
        // Closes last week's battle (recording the winner) and opens this week's
        rivalryService.rollOver();
    }

    private Exercise exercise(String name, String muscleGroup, String equipment) {
        return exercises.save(new Exercise(name, muscleGroup, equipment));
    }

    private AppUser member(String username, String displayName, AppUser trainer) {
        AppUser user = new AppUser(username, encoder.encode("password"), displayName, Role.MEMBER);
        user.setTrainer(trainer);
        return users.save(user);
    }

    // Creates one workout per "days ago" value, oldest first, slowly getting heavier
    private void seedHistory(AppUser user, Exercise[] lifts, double strength, int... daysAgo) {
        double[] startWeights = {60, 45, 40};
        boolean beatRecord = false;

        for (int i = 0; i < daysAgo.length; i++) {
            LocalDate date = LocalDate.now().minusDays(daysAgo[i]);
            WorkoutLog log = new WorkoutLog(user, date, 45, 3 + (i % 3));

            for (int lift = 0; lift < lifts.length; lift++) {
                // Add 2.5 kg every second workout
                double weight = Math.round(startWeights[lift] * strength / 2.5) * 2.5 + (i / 2) * 2.5;
                for (int set = 0; set < 3; set++) {
                    log.addSet(new SetEntry(lifts[lift], 8, weight));
                }
            }
            workoutLogs.save(log);
            progressService.updateStreak(user, date);
            if (progressService.updateRecords(log) > 0) {
                beatRecord = true;
            }
        }
        if (beatRecord) {
            progressService.award(user, BadgeType.FIRST_PR);
        }
        progressService.checkBadges(user);
    }
}
