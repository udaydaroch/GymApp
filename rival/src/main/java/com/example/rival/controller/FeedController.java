package com.example.rival.controller;

import com.example.rival.model.AppUser;
import com.example.rival.service.FeedService;
import com.example.rival.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class FeedController {

    private final UserService userService;
    private final FeedService feedService;

    public FeedController(UserService userService, FeedService feedService) {
        this.userService = userService;
        this.feedService = feedService;
    }

    // /feed shows everyone; /feed?filter=following shows only people you follow
    @GetMapping("/feed")
    public String feed(@RequestParam(name = "filter", required = false) String filter,
                       Principal principal, Model model) {
        AppUser me = userService.current(principal);
        boolean followingOnly = "following".equals(filter);
        model.addAttribute("items", feedService.feed(me, followingOnly));
        model.addAttribute("followingOnly", followingOnly);
        return "feed";
    }

    @PostMapping("/feed/{id}/like")
    public String like(@PathVariable("id") Long id,
                       @RequestParam(name = "filter", required = false) String filter,
                       Principal principal) {
        feedService.toggleLike(userService.current(principal), id);
        return "following".equals(filter) ? "redirect:/feed?filter=following" : "redirect:/feed";
    }
}
