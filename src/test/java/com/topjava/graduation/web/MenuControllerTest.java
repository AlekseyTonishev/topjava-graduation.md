package com.topjava.graduation.web;

import com.topjava.graduation.TestUtil;
import com.topjava.graduation.data.MenuTestData;
import com.topjava.graduation.data.RestaurantTestData;
import com.topjava.graduation.data.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.topjava.graduation.model.Menu;
import com.topjava.graduation.to.MenuTo;
import com.topjava.graduation.web.json.JsonUtil;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.topjava.graduation.data.MenuTestData.assertMatch;
import static com.topjava.graduation.util.MenuUtil.menuFromTo;
import static com.topjava.graduation.util.exception.ErrorType.DATA_ERROR;
import static com.topjava.graduation.util.exception.ErrorType.VALIDATION_ERROR;
import static com.topjava.graduation.web.MenuController.RESTAURANT_URL;

class MenuControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MenuController.REST_URL + '/';

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MenuTestData.contentJson(MenuTestData.MENU_1, MenuTestData.MENU_2, MenuTestData.MENU_5, MenuTestData.MENU_3, MenuTestData.MENU_4));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + MenuTestData.MENU_ID_1 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MenuTestData.contentJson(MenuTestData.MENU_1));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + 0 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_2)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithLocation() throws Exception {
        MenuTo menuTo = new MenuTo(null, LocalDate.of(2500, 1, 1));
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_2)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        Menu returned = TestUtil.readFromJson(action, Menu.class);
        menuTo.setId(returned.getId());

        Menu expected = menuFromTo(menuTo);
        MenuTestData.assertMatch(menuService.getAll(), MenuTestData.MENU_1, MenuTestData.MENU_2, MenuTestData.MENU_5, MenuTestData.MENU_3, MenuTestData.MENU_4, expected);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MenuTestData.MENU_ID_1 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        MenuTestData.assertMatch(menuService.getAll(), MenuTestData.MENU_2, MenuTestData.MENU_5, MenuTestData.MENU_3, MenuTestData.MENU_4);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + 0 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_2)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        MenuTo updated = new MenuTo(MenuTestData.MENU_ID_1, LocalDate.of(2500, 1, 1));
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MenuTestData.MENU_ID_1 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        Menu expected = menuFromTo(updated);
        expected.setRestaurant(RestaurantTestData.RESTAURANT_1);
        assertMatch(menuService.get(MenuTestData.MENU_ID_1, RestaurantTestData.RESTAURANT_ID_1), expected);
    }

    @Test
    void findByDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "byDate?date=" + LocalDate.now()))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MenuTestData.contentJson(MenuTestData.MENU_3, MenuTestData.MENU_4));
    }

    @Test
    void findByRestaurant() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "byRestaurant?name=" + RestaurantTestData.RESTAURANT_2.getName())
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MenuTestData.contentJson(MenuTestData.MENU_2, MenuTestData.MENU_3));
    }

    @Test
    void findByRestaurantAndDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "byRestaurantAndDate?name=" + RestaurantTestData.RESTAURANT_2.getName()
                + "&date=" + LocalDate.now())
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MenuTestData.contentJson(MenuTestData.MENU_3));
    }

    @Test
    void findByRestaurantAndDateNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "byRestaurantAndDate?name=" + RestaurantTestData.RESTAURANT_1.getName()
                + "&date=" + LocalDate.now())
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createInvalid() throws Exception {
        MenuTo invalid = new MenuTo(null, null);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    void updateInvalid() throws Exception {
        MenuTo invalid = new MenuTo(MenuTestData.MENU_ID_2, null);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MenuTestData.MENU_ID_2 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        MenuTo invalid = new MenuTo(MenuTestData.MENU_ID_2, LocalDate.now());
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MenuTestData.MENU_ID_2 + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        MenuTo invalid = new MenuTo(null, LocalDate.now());
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + RESTAURANT_URL + RestaurantTestData.RESTAURANT_ID_3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR));
    }
}