package com.topjava.graduation.data;

import com.topjava.graduation.TestUtil;
import org.springframework.test.web.servlet.ResultMatcher;
import com.topjava.graduation.model.Dish;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.topjava.graduation.model.AbstractBaseEntity.START_SEQ;

public class DishTestData {
    public static final int DISH_ID_1 = START_SEQ + 10;
    public static final int DISH_ID_2 = START_SEQ + 11;
    public static final int DISH_ID_3 = START_SEQ + 12;
    public static final int DISH_ID_4 = START_SEQ + 13;
    public static final int DISH_ID_5 = START_SEQ + 14;
    public static final int DISH_ID_6 = START_SEQ + 15;

    public static final Dish DISH_1 = new Dish(DISH_ID_1, "Steak", 100000);
    public static final Dish DISH_2 = new Dish(DISH_ID_2, "Hamburger", 10000);
    public static final Dish DISH_3 = new Dish(DISH_ID_3, "Bugs", 20000);
    public static final Dish DISH_4 = new Dish(DISH_ID_4, "BigSteak", 11100);
    public static final Dish DISH_5 = new Dish(DISH_ID_5, "BigVine", 22200);
    public static final Dish DISH_6 = new Dish(DISH_ID_6, "Takoburger", 33300);

    public static void assertMatch(Dish actual, Dish expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "menu");
    }

    public static void assertMatch(Iterable<Dish> actual, Dish... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<Dish> actual, Iterable<Dish> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("menu").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Dish... expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, Dish.class), List.of(expected));
    }

    public static ResultMatcher contentJson(Dish expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, Dish.class), expected);
    }
}