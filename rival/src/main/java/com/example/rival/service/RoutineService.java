package com.example.rival.service;

import com.example.rival.dto.RoutineForm;
import com.example.rival.dto.RoutineRow;
import com.example.rival.dto.SetRow;
import com.example.rival.dto.WorkoutForm;
import com.example.rival.model.AppUser;
import com.example.rival.model.Exercise;
import com.example.rival.model.Routine;
import com.example.rival.model.RoutineExercise;
import com.example.rival.repository.ExerciseRepository;
import com.example.rival.repository.RoutineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    public RoutineService(RoutineRepository routineRepository, ExerciseRepository exerciseRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<Routine> mine(AppUser user) {
        return routineRepository.findByOwnerOrderByIdDesc(user);
    }

    public List<Routine> sharedByOthers(AppUser user) {
        return routineRepository.findBySharedTrueAndOwnerNotOrderByTimesCopiedDescIdDesc(user);
    }

    // A routine can be viewed by its owner, or by anyone if it is shared
    public Routine getVisible(Long id, AppUser user) {
        Routine routine = routineRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!routine.isShared() && !isOwner(routine, user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return routine;
    }

    public boolean isOwner(Routine routine, AppUser user) {
        return routine.getOwner().getId().equals(user.getId());
    }

    @Transactional
    public Routine create(AppUser owner, RoutineForm form) {
        Routine routine = new Routine(form.getName().trim(), form.getDescription(), owner, form.isShared());
        for (RoutineRow row : form.filledInRows()) {
            Exercise exercise = exerciseRepository.findById(row.getExerciseId()).orElse(null);
            if (exercise != null) {
                routine.addItem(new RoutineExercise(exercise, clamp(row.getSets(), 3, 10), clamp(row.getReps(), 10, 100)));
            }
        }
        return routineRepository.save(routine);
    }

    // Makes a private copy of someone's shared routine for this user
    @Transactional
    public Routine copy(Long id, AppUser user) {
        Routine original = getVisible(id, user);

        // Prototype pattern: the routine clones itself, including its exercise lines
        Routine copy = original.copy();
        copy.setOwner(user);

        original.setTimesCopied(original.getTimesCopied() + 1);
        routineRepository.save(original);
        return routineRepository.save(copy);
    }

    @Transactional
    public void toggleShared(Long id, AppUser user) {
        Routine routine = getOwned(id, user);
        routine.setShared(!routine.isShared());
        routineRepository.save(routine);
    }

    @Transactional
    public void delete(Long id, AppUser user) {
        Routine routine = getOwned(id, user);
        // Copies made from this routine keep existing, they just lose the link back
        for (Routine copy : routineRepository.findByCopiedFrom(routine)) {
            copy.setCopiedFrom(null);
            routineRepository.save(copy);
        }
        routineRepository.delete(routine);
    }

    // Turns a routine into a pre-filled workout form, one row per set (up to the form's row limit)
    public WorkoutForm toWorkoutForm(Routine routine, Map<Long, Double> bestWeights) {
        WorkoutForm form = new WorkoutForm();
        for (RoutineExercise item : routine.getItems()) {
            Double weight = bestWeights.getOrDefault(item.getExercise().getId(), 0.0);
            for (int i = 0; i < item.getSets() && !form.isFull(); i++) {
                form.getRows().add(new SetRow(item.getExercise().getId(), item.getReps(), weight));
            }
        }
        form.addBlankRows(2);
        return form;
    }

    public int totalSets(Routine routine) {
        int total = 0;
        for (RoutineExercise item : routine.getItems()) {
            total += item.getSets();
        }
        return total;
    }

    private Routine getOwned(Long id, AppUser user) {
        Routine routine = routineRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!isOwner(routine, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return routine;
    }

    // Uses the fallback when empty, and keeps the number between 1 and max
    private int clamp(Integer value, int fallback, int max) {
        if (value == null) {
            return fallback;
        }
        return Math.max(1, Math.min(value, max));
    }
}
