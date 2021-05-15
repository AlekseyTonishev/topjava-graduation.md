package com.topjava.graduation.service;

import com.topjava.graduation.repository.DishRepository;
import com.topjava.graduation.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.topjava.graduation.model.Dish;

import java.time.LocalDate;
import java.util.List;

import static com.topjava.graduation.util.ValidationUtil.checkNotFound;
import static com.topjava.graduation.util.ValidationUtil.checkNotFoundWithId;

@Service
public class DishService {
    private static final Sort SORT_NAME_PRICE = new Sort(Sort.Direction.ASC, "name", "price");

    private final DishRepository repository;

    private final MenuRepository menuRepository;

    @Autowired
    public DishService(DishRepository repository, MenuRepository menuRepository) {
        this.repository = repository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public Dish create(Dish dish, int menuId) {
        Assert.notNull(dish, "dish must not be null");
        if (!dish.isNew() && get(dish.getId(), menuId) == null) {
            return null;
        }
        dish.setMenu(menuRepository.getOne(menuId));
        return repository.save(dish);
    }

    @Transactional
    public void update(Dish dish, int menuId) {
        Assert.notNull(dish, "dish must not be null");
        dish.setMenu(menuRepository.getOne(menuId));
        checkNotFoundWithId(repository.save(dish), dish.getId());
    }

    @Transactional
    public void delete(int id, int menuId) {
        checkNotFoundWithId(repository.delete(id, menuId) != 0, id);
    }

    public Dish get(int id, int menuId) {
        return checkNotFoundWithId(repository.findById(id)
                .filter(d -> d.getMenu().getId() == menuId)
                .orElse(null), id);
    }

    public List<Dish> findByDate(LocalDate date) {
        Assert.notNull(date, "date must not be null");
        List<Dish> dishList = repository.findByDate(date);
        checkNotFound(!dishList.isEmpty(), date.toString());
        return dishList;
    }

    public List<Dish> findByMenu(int menuId) {
        List<Dish> dishList = repository.findByMenu(menuId);
        checkNotFoundWithId(!dishList.isEmpty(), menuId);
        return dishList;
    }

    public List<Dish> getAll() {
        return repository.findAll(SORT_NAME_PRICE);
    }
}