package com.topjava.graduation.web;

import com.topjava.graduation.model.Vote;
import com.topjava.graduation.service.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String REST_URL = "/rest/votes";

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/byDate")
    public List<Vote> findAllByDate(@RequestParam LocalDate date) {
        date = Objects.isNull(date) ? LocalDate.now() : date;
        log.info("getAllByDate {}", date);
        return voteService.getAllByDate(date);
    }

    @PostMapping("menus/{menuId}")
    @ResponseStatus(HttpStatus.OK)
    public void vote(@PathVariable int menuId) {
        int userId = SecurityUtil.authUserId();
        LocalDate today = LocalDate.now();
        log.info("User with id = {} votes for menu with id = {}", userId, menuId);
        voteService.create(today, userId, menuId);
    }

    @PutMapping("menus/{menuId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateVote(@PathVariable int menuId) {
        int userId = SecurityUtil.authUserId();
        log.info("User with id = {} updates vote for menu with id = {}", userId, menuId);
        LocalDate today = LocalDate.now();
        Vote vote = voteService.getForUserAndDate(userId, today);
        voteService.update(vote, userId, menuId);
    }
}