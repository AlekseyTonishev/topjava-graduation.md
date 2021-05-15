package com.topjava.graduation.service;

import com.topjava.graduation.repository.UserRepository;
import com.topjava.graduation.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.topjava.graduation.model.Menu;
import com.topjava.graduation.model.Vote;

import java.time.LocalDate;
import java.util.List;

import static com.topjava.graduation.util.ValidationUtil.*;

@Service
public class VoteService {
    private static final Sort SORT_DATE = new Sort(Sort.Direction.ASC, "date");

    private final VoteRepository repository;
    private final MenuService menuService;
    private final UserRepository userRepository;

    @Autowired
    public VoteService(VoteRepository repository, UserRepository userRepository, MenuService menuService) {
        this.repository = repository;
        this.menuService = menuService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Vote create(LocalDate date, int userId, int menuId) {
        Assert.notNull(date, "date must not be null");
        Menu menu = menuService.findById(menuId);
        checkExpiredDate(menu.getDate(), menuId);
        Vote vote = new Vote(null, date);
        vote.setUser(userRepository.getOne(userId));
        vote.setMenu(menu);
        return repository.save(vote);
    }

    @Transactional
    public void update(Vote vote, int userId, int menuId) {
        Assert.notNull(vote, "vote must not be null");
        Menu menu = menuService.findById(menuId);
        checkExpiredDateWithTime(menu.getDate(), menuId);
        vote.setUser(userRepository.getOne(userId));
        vote.setMenu(menu);
        checkNotFoundWithId(repository.save(vote), vote.getId());
    }

    public Vote get(int id, int userId, int menuId) {
        return checkNotFoundWithId(repository.get(id, userId, menuId), id);
    }

    public Vote getForUserAndDate(int userId, LocalDate date) {
        Assert.notNull(date, "date must not be null");
        return checkNotFoundWithId(repository.getForUserAndDate(userId, date)
                .orElse(null), userId);
    }

    public List<Vote> getAll() {
        return repository.findAll(SORT_DATE);
    }

    public List<Vote> getAllByDate(LocalDate date) {
        Assert.notNull(date, "date must not be null");
        return repository.getAllByDate(date);
    }
}