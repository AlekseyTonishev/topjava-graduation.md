package com.topjava.graduation.repository;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import com.topjava.graduation.model.Menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Menu entity.
 */
@Transactional(readOnly = true)
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT m FROM Menu m WHERE m.date=:date")
    List<Menu> findByDateWithDishes(@Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.name=:name")
    List<Menu> findByRestaurant(@Param("name") String name);

    @Query("SELECT m FROM Menu m WHERE m.restaurant.name=:name AND m.date=:date")
    Optional<Menu> findByRestaurantAndDate(@Param("name") String name, @Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    @Override
    @Transactional
    Menu save(Menu menu);

    @Modifying
    @Transactional
    @Query("DELETE FROM Menu m WHERE m.id=:id AND m.restaurant.id=:restaurant_id")
    int delete(@Param("id") int id, @Param("restaurant_id") int restaurant_id);

    @Query("SELECT m FROM Menu m WHERE m.id=:id AND m.restaurant.id=:restaurant_id")
    Optional<Menu> get(@Param("id") int id, @Param("restaurant_id") int restaurant_id);
}
