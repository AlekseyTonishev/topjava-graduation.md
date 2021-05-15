package com.topjava.graduation.web;

import com.topjava.graduation.TestUtil;
import com.topjava.graduation.data.DishTestData;
import com.topjava.graduation.data.MenuTestData;
import com.topjava.graduation.data.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.topjava.graduation.model.Dish;
import com.topjava.graduation.to.DishTo;
import com.topjava.graduation.web.json.JsonUtil;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.topjava.graduation.data.DishTestData.assertMatch;
import static com.topjava.graduation.util.exception.ErrorType.DATA_ERROR;
import static com.topjava.graduation.util.exception.ErrorType.VALIDATION_ERROR;
import static com.topjava.graduation.web.DishController.MENUS_URL;

class DishControllerTest extends AbstractControllerTest {

    private static final String REST_URL = DishController.REST_URL + '/';

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.contentJson(DishTestData.DISH_3, DishTestData.DISH_2, DishTestData.DISH_4, DishTestData.DISH_5, DishTestData.DISH_1, DishTestData.DISH_6));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + DishTestData.DISH_ID_1 + MENUS_URL + MenuTestData.MENU_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.contentJson(DishTestData.DISH_1));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + DishTestData.DISH_ID_3 + MENUS_URL + MenuTestData.MENU_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithLocation() throws Exception {
        Dish expected = new Dish(null, "New dish", 10000);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_URL + MenuTestData.MENU_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected)))
                .andExpect(status().isCreated());

        Dish returned = TestUtil.readFromJson(action, Dish.class);
        expected.setId(returned.getId());

        DishTestData.assertMatch(returned, expected);
        DishTestData.assertMatch(dishService.getAll(), DishTestData.DISH_3, DishTestData.DISH_2, DishTestData.DISH_4, DishTestData.DISH_5, expected, DishTestData.DISH_1, DishTestData.DISH_6);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + DishTestData.DISH_ID_6 + MENUS_URL + MenuTestData.MENU_ID_4)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        DishTestData.assertMatch(dishService.getAll(), DishTestData.DISH_3, DishTestData.DISH_2, DishTestData.DISH_4, DishTestData.DISH_5, DishTestData.DISH_1);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + DishTestData.DISH_ID_3 + MENUS_URL + MenuTestData.MENU_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        Dish updated = new Dish(DishTestData.DISH_1);
        updated.setName("UpdatedName");
        updated.setPrice(100);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DishTestData.DISH_ID_1 + MENUS_URL + MenuTestData.MENU_ID_1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        assertMatch(dishService.get(DishTestData.DISH_ID_1, MenuTestData.MENU_ID_1), updated);
    }

    @Test
    void findByDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by?date=" + LocalDate.now())
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.contentJson(DishTestData.DISH_3, DishTestData.DISH_4, DishTestData.DISH_5, DishTestData.DISH_6));
    }

    @Test
    void findByMenu() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + MENUS_URL + MenuTestData.MENU_ID_3)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.contentJson(DishTestData.DISH_3, DishTestData.DISH_4, DishTestData.DISH_5));
    }

    @Test
    void createInvalid() throws Exception {
        Dish invalid = new Dish(null, "", 200);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_URL + MenuTestData.MENU_ID_1)
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
        Dish invalid = new Dish(DishTestData.DISH_1);
        invalid.setName("");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DishTestData.DISH_ID_1 + MENUS_URL + MenuTestData.MENU_ID_1)
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
        DishTo invalid = new DishTo(DishTestData.DISH_ID_3, "BigSteak", 6000);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DishTestData.DISH_ID_3 + MENUS_URL + MenuTestData.MENU_ID_3)
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
        DishTo invalid = new DishTo(null, "BigSteak", 6000);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_URL + MenuTestData.MENU_ID_3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR));
    }

    @Test
    void updateHtmlUnsafe() throws Exception {
        Dish invalid = new Dish(DishTestData.DISH_ID_1, "<script>alert(123)</script>", 200);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DishTestData.DISH_ID_1 + MENUS_URL + MenuTestData.MENU_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }
}