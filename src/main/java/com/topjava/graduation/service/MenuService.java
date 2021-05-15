package com.topjava.graduation.service;

import com.topjava.graduation.repository.MenuRepository;
import com.topjava.graduation.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.topjava.graduation.model.Menu;

import java.time.LocalDate;
import java.util.List;

import static com.topjava.graduation.util.ValidationUtil.checkNotFound;
import static com.topjava.graduation.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MenuService {
    private static final Sort SORT_DATE = new Sort(Sort.Direction.ASC, "date");

    private final MenuRepository repository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MenuService(MenuRepository repository, RestaurantRepository restaurantRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    @CacheEvict(value = "menusByDate", allEntries = true)
    @Transactional
    public Menu create(Menu menu, int restaurant_id) {
        Assert.notNull(menu, "menu must not be null");
        if (!menu.isNew() && get(menu.getId(), restaurant_id) == null) {
            return null;
        }
        menu.setRestaurant(restaurantRepository.getOne(restaurant_id));
        return repository.save(menu);
    }

    @CacheEvict(value = "menusByDate", allEntries = true)
    @Transactional
    public void update(Menu menu, int restaurant_id) {
        Assert.notNull(menu, "menu must not be null");
        menu.setRestaurant(restaurantRepository.getOne(restaurant_id));
        checkNotFoundWithId(repository.save(menu), menu.getId());
    }

    public Menu get(int id, int restaurant_id) {
        return checkNotFoundWithId(repository.get(id, restaurant_id)
                .orElse(null), id);
    }

    public Menu findByRestaurantAndDate(String name, LocalDate date) {
        Assert.notNull(name, "name must not be null");
        Assert.notNull(date, "date must not be null");
        return checkNotFound(repository.findByRestaurantAndDate(name, date)
                .orElse(null), "name=" + name + "and date=" + date);
    }

    public Menu findById(int id) {
        return checkNotFoundWithId(repository.findById(id)
                .orElse(null), id);
    }

    @Cacheable("menusByDate")
    public List<Menu> findByDateWithDishes(LocalDate date) {
        Assert.notNull(date, "date must not be null");
        return repository.findByDateWithDishes(date);
    }

    public List<Menu> findByRestaurant(String name) {
        Assert.notNull(name, "name must not be null");
        return repository.findByRestaurant(name);
    }

    @CacheEvict(value = "menusByDate", allEntries = true)
    @Transactional
    public void delete(int id, int restaurant_id) {
        checkNotFoundWithId(repository.delete(id, restaurant_id) != 0, id);
    }

    public List<Menu> getAll() {
        return repository.findAll(SORT_DATE);
    }
}