package com.topjava.graduation.util;

import com.topjava.graduation.model.Dish;
import com.topjava.graduation.to.DishTo;

public class DishUtil {

    private DishUtil() {
    }

    public static Dish dishFromTo(DishTo dishTo) {
        return new Dish(dishTo.getId(), dishTo.getName(), dishTo.getPrice());
    }
}