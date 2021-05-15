package com.topjava.graduation.web;

import com.topjava.graduation.model.Menu;
import com.topjava.graduation.service.MenuService;
import com.topjava.graduation.util.MenuUtil;
import com.topjava.graduation.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.topjava.graduation.to.MenuTo;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = MenuController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String REST_URL = "/rest/menus";
    public static final String RESTAURANT_URL = "/restaurants/";

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<Menu> getAll() {
        log.info("getAll");
        return menuService.getAll();
    }

    @GetMapping("/{id}" + RESTAURANT_URL + "{restaurant_id}")
    public Menu get(@PathVariable int id, @PathVariable int restaurant_id) {
        log.info("get {} for id={}", id, restaurant_id);
        return menuService.get(id, restaurant_id);
    }


    @PostMapping(value = RESTAURANT_URL + "{restaurant_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> createWithLocation(@Valid @RequestBody MenuTo menuTo, @PathVariable int restaurant_id) {
        log.info("create {} for id={}", menuTo, restaurant_id);
        ValidationUtil.checkNew(menuTo);
        Menu created = menuService.create(MenuUtil.menuFromTo(menuTo), restaurant_id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL)
                .buildAndExpand(restaurant_id, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}" + RESTAURANT_URL + "{restaurant_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @PathVariable int restaurant_id) {
        log.info("delete {} for id={}", id, restaurant_id);
        menuService.delete(id, restaurant_id);
    }

    @PutMapping(value = "/{id}" + RESTAURANT_URL + "{restaurant_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody MenuTo menuTo, @PathVariable int id, @PathVariable int restaurant_id) {
        log.info("update {} with id={} for id={}", menuTo, id, restaurant_id);
        ValidationUtil.assureIdConsistent(menuTo, id);
        menuService.update(MenuUtil.menuFromTo(menuTo), restaurant_id);
    }

    @GetMapping("/byDate")
    public List<Menu> findByDateWithDishes(@RequestParam LocalDate date) {
        date = Objects.isNull(date) ? LocalDate.now() : date;
        log.info("findByDateWithDishes {}", date);
        return menuService.findByDateWithDishes(date);
    }

    @GetMapping("/byRestaurant")
    public List<Menu> findByRestaurant(@RequestParam String name) {
        log.info("findByRestaurant {}", name);
        return menuService.findByRestaurant(name);
    }

    @GetMapping("/byRestaurantAndDate")
    public Menu findByRestaurantAndDate(@RequestParam String name, @RequestParam LocalDate date) {
        date = Objects.isNull(date) ? LocalDate.now() : date;
        log.info("findByRestaurantAndDate for name={} and date={}", name, date);
        return menuService.findByRestaurantAndDate(name, date);
    }

    @GetMapping("/byId")
    public Menu findById(@RequestParam int id) {
        log.info("findById {}", id);
        return menuService.findById(id);
    }
}