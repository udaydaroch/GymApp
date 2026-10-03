package com.example.rival.controller;

import com.example.rival.dto.WorkoutForm;
import com.example.rival.model.AppUser;
import com.example.rival.service.UserService;
import com.example.rival.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class WorkoutController {

    private final UserService userService;
    private final WorkoutService workoutService;

    public WorkoutController(UserService userService, WorkoutService workoutService) {
        this.userService = userService;
        this.workoutService = workoutService;
    }

    @GetMapping("/workouts")
    public String history(Principal principal, Model model) {
        model.addAttribute("logs", workoutService.history(userService.current(principal)));
        return "workouts";
    }

    @GetMapping("/workouts/new")
    public String newWorkout(Model model) {
        model.addAttribute("workoutForm", WorkoutForm.withBlankRows(8));
        model.addAttribute("exercises", workoutService.allExercises());
        return "workout-form";
    }

    // The "Add another row" button submits the form with an extra addRow parameter.
    // Nothing is saved: the page comes back with what was typed plus one blank row.
    @PostMapping(value = "/workouts", params = "addRow")
    public String addRow(@ModelAttribute("workoutForm") WorkoutForm form, BindingResult result, Model model) {
        form.addBlankRows(1);
        model.addAttribute("exercises", workoutService.allExercises());
        return "workout-form";
    }

    // Each row's "Remove" button submits the form with removeRow = that row's position
    @PostMapping(value = "/workouts", params = "removeRow")
    public String removeRow(@ModelAttribute("workoutForm") WorkoutForm form, BindingResult result,
                            @RequestParam("removeRow") int index, Model model) {
        form.removeRow(index);
        model.addAttribute("exercises", workoutService.allExercises());
        return "workout-form";
    }

    // The same form is also submitted from a routine and from "No Excuses"
    @PostMapping("/workouts")
    public String logWorkout(@Valid @ModelAttribute("workoutForm") WorkoutForm form,
                             BindingResult result,
                             Principal principal,
                             Model model,
                             RedirectAttributes redirect) {
        if (form.filledInRows().isEmpty()) {
            result.reject("empty", "Add at least one set: pick an exercise and enter the reps.");
        }
        if (form.getRows().size() > WorkoutForm.MAX_ROWS) {
            result.reject("tooMany", "A workout can have at most " + WorkoutForm.MAX_ROWS + " rows.");
        }
        if (result.hasErrors()) {
            model.addAttribute("exercises", workoutService.allExercises());
            return "workout-form";
        }
        AppUser me = userService.current(principal);
        workoutService.logWorkout(me, form);
        redirect.addFlashAttribute("message", "Workout logged. Streak: " + me.getActiveStreak() + " day(s).");
        return "redirect:/";
    }
}
