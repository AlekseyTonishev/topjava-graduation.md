package com.topjava.graduation.web.user;

import com.topjava.graduation.TestUtil;
import com.topjava.graduation.data.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.topjava.graduation.model.Role;
import com.topjava.graduation.model.User;
import com.topjava.graduation.util.exception.ErrorType;
import com.topjava.graduation.web.AbstractControllerTest;
import com.topjava.graduation.web.json.JsonUtil;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.topjava.graduation.data.UserTestData.*;
import static com.topjava.graduation.util.exception.ErrorType.DATA_ERROR;
import static com.topjava.graduation.util.exception.ErrorType.VALIDATION_ERROR;


class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Test
    void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + UserTestData.ADMIN_ID)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(UserTestData.contentJson(UserTestData.ADMIN));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + 1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void getByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by?email=" + UserTestData.USER.getEmail())
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(UserTestData.contentJson(UserTestData.USER));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + UserTestData.USER_ID)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(userService.getAll(), UserTestData.ADMIN);
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + 1)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void getUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(TestUtil.userHttpBasic(UserTestData.USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update() throws Exception {
        User updated = new User(UserTestData.USER);
        updated.setName("UpdatedName");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + UserTestData.USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        assertMatch(userService.get(UserTestData.USER_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected)))
                .andExpect(status().isCreated());

        User returned = TestUtil.readFromJson(action, User.class);
        expected.setId(returned.getId());

        UserTestData.assertMatch(returned, expected);
        UserTestData.assertMatch(userService.getAll(), UserTestData.ADMIN, expected, UserTestData.USER);
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(UserTestData.contentJson(UserTestData.ADMIN, UserTestData.USER));
    }

    @Test
    void createInvalid() throws Exception {
        User created = new User(null, "", "new@mail.com", "password", Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .content(UserTestData.jsonWithPassword(created, "password")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR));
    }

    @Test
    void updateInvalid() throws Exception {
        User updated = new User(UserTestData.USER);
        updated.setName("");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + UserTestData.USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        User updated = new User(UserTestData.USER);
        updated.setEmail("admin@mail.com");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + UserTestData.USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .content(UserTestData.jsonWithPassword(updated, "password")))
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        User created = new User(null, "newUser", "admin@mail.com", "password", Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN))
                .content(UserTestData.jsonWithPassword(created, "password")))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR));
    }
}