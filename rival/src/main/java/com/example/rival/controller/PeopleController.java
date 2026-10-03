package com.example.rival.controller;

import com.example.rival.exception.RivalRequestException;
import com.example.rival.model.AppUser;
import com.example.rival.service.FollowService;
import com.example.rival.service.RivalryService;
import com.example.rival.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class PeopleController {

    private final UserService userService;
    private final FollowService followService;
    private final RivalryService rivalryService;

    public PeopleController(UserService userService, FollowService followService,
                            RivalryService rivalryService) {
        this.userService = userService;
        this.followService = followService;
        this.rivalryService = rivalryService;
    }

    @GetMapping("/people")
    public String people(Principal principal, Model model) {
        model.addAttribute("people", followService.people(userService.current(principal)));
        return "people";
    }

    // "from" says which page the button was on, so we can go back to it
    @PostMapping("/people/{id}/follow")
    public String follow(@PathVariable("id") Long id,
                         @RequestParam(name = "from", required = false) String from,
                         Principal principal, RedirectAttributes redirect) {
        AppUser followed = followService.follow(userService.current(principal), id);
        redirect.addFlashAttribute("message", "You're now following " + followed.getDisplayName() + ".");
        return "feed".equals(from) ? "redirect:/feed" : "redirect:/people";
    }

    @PostMapping("/people/{id}/unfollow")
    public String unfollow(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        AppUser unfollowed = followService.unfollow(userService.current(principal), id);
        redirect.addFlashAttribute("message", "You unfollowed " + unfollowed.getDisplayName() + ".");
        return "redirect:/people";
    }

    @PostMapping("/people/{id}/rival-request")
    public String rivalRequest(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            AppUser to = rivalryService.sendRequest(userService.current(principal), id);
            redirect.addFlashAttribute("message", "Rival request sent to " + to.getDisplayName() + ".");
        } catch (RivalRequestException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/people";
    }
}
