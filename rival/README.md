# Rival

A workout tracker where members log workouts, share routines, and battle a rival each week,
and trainers see which clients are drifting away.

Spring Boot 3 + Thymeleaf + Spring Security + Spring Data JPA + H2, built with Gradle.

## Run with Docker

    docker compose up --build

Then open http://localhost:8080

## Logins

| Username | Password | Role    | Notes                                   |
|----------|----------|---------|-----------------------------------------|
| alex     | password | Member  | 3-day streak, rival is Sam              |
| sam      | password | Member  | Rival is Alex, shares a routine         |
| jordan   | password | Member  | No rival; has a rival request from Riley |
| riley    | password | Member  | Never trained; has asked Jordan          |
| coach    | password | Trainer | Has the "Clients" page                  |
| admin    | admin    | Admin   | Can open the H2 console                 |

You can also create new accounts from the login page.

## H2 console

Log in as `admin`, then open http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:rivaldb`
- User Name: `sa`
- Password: (leave empty)

Tables: `USERS`, `EXERCISES`, `WORKOUT_LOGS`, `SET_ENTRIES`, `PERSONAL_RECORDS`, `ROUTINES`,
`ROUTINE_EXERCISES`, `RIVALRIES`, `RIVAL_REQUESTS`, `WEEKLY_BATTLES`, `BADGES`, `FOLLOWS`,
`WORKOUT_LIKES`.

The database is in memory, so it resets to the demo data every time the app restarts.

## Social features

- **Feed** (`/feed`): every logged workout is posted here unless you untick "Post this workout to the
  feed". Anyone can like a post. The "Following" tab shows only people you follow.
- **People** (`/people`): follow or unfollow other users.
- **Rival requests**: once you follow someone you can send them a rival request. The rivalry starts
  only when they accept it on their Rival page. "Find me a rival" sends a request to the closest match.

## Code layout

    com.example.rival
    ├── model/        JPA entities (one per table) and enums
    ├── repository/   Spring Data JPA interfaces (one per entity)
    ├── service/      all business rules
    ├── controller/   web pages: receive the request, call a service, pick a template
    ├── dto/          form objects and page view objects
    ├── scoring/      battle scoring rules (Strategy pattern)
    ├── builder/      "No Excuses" workout builder (Builder pattern)
    ├── event/        "workout logged" event and its listeners (Observer pattern)
    ├── exception/    errors with a message that is safe to show the user
    └── config/       SecurityConfig, DataSeeder

Every request follows: Controller -> Service -> Repository -> H2.

## Design patterns and where to find them

| Pattern    | Where                                   | What it does                                                        |
|------------|-----------------------------------------|---------------------------------------------------------------------|
| Repository | `repository/`                           | Database access without writing SQL                                 |
| Service    | `service/`                              | Keeps rules out of controllers and templates                        |
| DTO        | `dto/`                                  | Forms bind to these, never straight to entities                     |
| Strategy   | `scoring/`                              | Each battle type is one class; `RivalryService` picks one by type   |
| Builder    | `builder/WorkoutPlanBuilder`            | Assembles a workout from time, energy, equipment and sore areas     |
| Prototype  | `Routine.copy()`, `RoutineExercise.copy()` | A routine clones itself when another user copies it              |
| Observer   | `event/`                                | Logging a workout fires an event; streaks, records, badges react    |
| Scheduler  | `RivalryService.rollOver()`             | Closes last week's battles every Monday                             |

## Run without Docker

Needs JDK 17+.

    ./gradlew bootRun        (Windows: gradlew.bat bootRun)
