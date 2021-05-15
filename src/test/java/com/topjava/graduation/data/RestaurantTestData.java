package com.topjava.graduation.data;

import com.topjava.graduation.TestUtil;
import org.springframework.test.web.servlet.ResultMatcher;
import com.topjava.graduation.model.Restaurant;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.topjava.graduation.model.AbstractBaseEntity.START_SEQ;

public class RestaurantTestData {
    public static final int RESTAURANT_ID_1 = START_SEQ + 2;
    public static final int RESTAURANT_ID_2 = START_SEQ + 3;
    public static final int RESTAURANT_ID_3 = START_SEQ + 4;

    public static final Restaurant RESTAURANT_1 = new Restaurant(RESTAURANT_ID_1, "Room");
    public static final Restaurant RESTAURANT_2 = new Restaurant(RESTAURANT_ID_2, "Pizza");
    public static final Restaurant RESTAURANT_3 = new Restaurant(RESTAURANT_ID_3, "McDonalds");

    public static void assertMatch(Restaurant actual, Restaurant expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Restaurant> actual, Restaurant... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<Restaurant> actual, Iterable<Restaurant> expected) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Restaurant... expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, Restaurant.class), List.of(expected));
    }

    public static ResultMatcher contentJson(Restaurant expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, Restaurant.class), expected);
    }
}