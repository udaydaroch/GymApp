package com.example.rival.controller;

import com.example.rival.dto.RoutineForm;
import com.example.rival.dto.WorkoutForm;
import com.example.rival.model.AppUser;
import com.example.rival.model.Routine;
import com.example.rival.service.ProgressService;
import com.example.rival.service.RoutineService;
import com.example.rival.service.UserService;
import com.example.rival.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class RoutineController {

    private final UserService userService;
    private final RoutineService routineService;
    private final WorkoutService workoutService;
    private final ProgressService progressService;

    public RoutineController(UserService userService, RoutineService routineService,
                             WorkoutService workoutService, ProgressService progressService) {
        this.userService = userService;
        this.routineService = routineService;
        this.workoutService = workoutService;
        this.progressService = progressService;
    }

    @GetMapping("/routines")
    public String list(Principal principal, Model model) {
        AppUser me = userService.current(principal);
        model.addAttribute("mine", routineService.mine(me));
        model.addAttribute("shared", routineService.sharedByOthers(me));
        return "routines";
    }

    @GetMapping("/routines/new")
    public String newRoutine(Model model) {
        model.addAttribute("routineForm", RoutineForm.withBlankRows(6));
        model.addAttribute("exercises", workoutService.allExercises());
        return "routine-form";
    }

    // "Add another exercise": nothing is saved, the page comes back with one more blank row
    @PostMapping(value = "/routines", params = "addRow")
    public String addRow(@ModelAttribute("routineForm") RoutineForm form, BindingResult result, Model model) {
        form.addBlankRows(1);
        model.addAttribute("exercises", workoutService.allExercises());
        return "routine-form";
    }

    // Each row's "Remove" button submits the form with removeRow = that row's position
    @PostMapping(value = "/routines", params = "removeRow")
    public String removeRow(@ModelAttribute("routineForm") RoutineForm form, BindingResult result,
                            @RequestParam("removeRow") int index, Model model) {
        form.removeRow(index);
        model.addAttribute("exercises", workoutService.allExercises());
        return "routine-form";
    }

    @PostMapping("/routines")
    public String create(@Valid @ModelAttribute("routineForm") RoutineForm form,
                         BindingResult result,
                         Principal principal,
                         Model model,
                         RedirectAttributes redirect) {
        if (form.filledInRows().isEmpty()) {
            result.reject("empty", "Pick at least one exercise.");
        }
        if (form.getRows().size() > RoutineForm.MAX_ROWS) {
            result.reject("tooMany", "A routine can have at most " + RoutineForm.MAX_ROWS + " exercises.");
        }
        if (result.hasErrors()) {
            model.addAttribute("exercises", workoutService.allExercises());
            return "routine-form";
        }
        Routine routine = routineService.create(userService.current(principal), form);
        redirect.addFlashAttribute("message", "Routine saved.");
        return "redirect:/routines/" + routine.getId();
    }

    @GetMapping("/routines/{id}")
    public String detail(@PathVariable("id") Long id, Principal principal, Model model) {
        AppUser me = userService.current(principal);
        Routine routine = routineService.getVisible(id, me);
        model.addAttribute("routine", routine);
        model.addAttribute("owner", routineService.isOwner(routine, me));
        return "routine-detail";
    }

    // Opens the workout form pre-filled with this routine's sets
    @GetMapping("/routines/{id}/start")
    public String start(@PathVariable("id") Long id, Principal principal, Model model) {
        AppUser me = userService.current(principal);
        Routine routine = routineService.getVisible(id, me);
        WorkoutForm form = routineService.toWorkoutForm(routine, progressService.bestWeights(me));
        String notice = "Pre-filled from \"" + routine.getName() + "\". Adjust anything, then log it.";
        if (routineService.totalSets(routine) > WorkoutForm.MAX_ROWS) {
            notice = "This routine has more sets than one workout can hold, so only the first "
                + WorkoutForm.MAX_ROWS + " are filled in.";
        }
        model.addAttribute("workoutForm", form);
        model.addAttribute("exercises", workoutService.allExercises());
        model.addAttribute("notice", notice);
        return "workout-form";
    }

    @PostMapping("/routines/{id}/copy")
    public String copy(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        Routine copy = routineService.copy(id, userService.current(principal));
        redirect.addFlashAttribute("message", "Copied to your routines.");
        return "redirect:/routines/" + copy.getId();
    }

    @PostMapping("/routines/{id}/share")
    public String toggleShare(@PathVariable("id") Long id, Principal principal) {
        routineService.toggleShared(id, userService.current(principal));
        return "redirect:/routines/" + id;
    }

    @PostMapping("/routines/{id}/delete")
    public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        routineService.delete(id, userService.current(principal));
        redirect.addFlashAttribute("message", "Routine deleted.");
        return "redirect:/routines";
    }
}
