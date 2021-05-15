package com.topjava.graduation.data;

import com.topjava.graduation.TestUtil;
import org.springframework.test.web.servlet.ResultMatcher;
import com.topjava.graduation.model.Menu;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.topjava.graduation.model.AbstractBaseEntity.START_SEQ;

public class MenuTestData {
    public static final int MENU_ID_1 = START_SEQ + 5;
    public static final int MENU_ID_2 = START_SEQ + 6;
    public static final int MENU_ID_3 = START_SEQ + 7;
    public static final int MENU_ID_4 = START_SEQ + 8;
    public static final int MENU_ID_5 = START_SEQ + 9;

    public static final Menu MENU_1 = new Menu(MENU_ID_1, LocalDate.of(2021, 4, 23));
    public static final Menu MENU_2 = new Menu(MENU_ID_2, LocalDate.of(2021, 4, 25));
    public static final Menu MENU_3 = new Menu(MENU_ID_3, LocalDate.now());
    public static final Menu MENU_4 = new Menu(MENU_ID_4, LocalDate.now());
    public static final Menu MENU_5 = new Menu(MENU_ID_5, LocalDate.of(2021, 5, 12));

    public static void assertMatch(Menu actual, Menu expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "dishes", "restaurant");
    }

    public static void assertMatch(Iterable<Menu> actual, Menu... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<Menu> actual, Iterable<Menu> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("dishes", "restaurant").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Menu... expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, Menu.class), List.of(expected));
    }

    public static ResultMatcher contentJson(Menu expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, Menu.class), expected);
    }
}