package com.topjava.graduation.web;

import com.topjava.graduation.TestUtil;
import com.topjava.graduation.data.MenuTestData;
import com.topjava.graduation.data.UserTestData;
import com.topjava.graduation.data.VoteTestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.topjava.graduation.model.Vote;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.topjava.graduation.data.VoteTestData.assertMatch;
import static com.topjava.graduation.data.VoteTestData.contentJson;

class VoteControllerTest extends AbstractControllerTest {

    private static final String REST_URL = VoteController.REST_URL + '/';
    private static final String MENUS_MAPPING = "menus/";

    @Test
    void voteExpiredDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_2)
                .with(TestUtil.userHttpBasic(UserTestData.USER)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void vote() throws Exception {
        LocalDate today = LocalDate.now();
        if (MenuTestData.MENU_3.getDate().isEqual(today)) {
            mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_3)
                    .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                    .andDo(print())
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_3)
                    .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void voteDuplicate() throws Exception {
        if (MenuTestData.MENU_3.getDate().isEqual(LocalDate.now())) {
            mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_3)
                    .with(TestUtil.userHttpBasic(UserTestData.USER)))
                    .andDo(print())
                    .andExpect(status().isConflict());
        } else {
            mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_3)
                    .with(TestUtil.userHttpBasic(UserTestData.USER)))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void updateVote() throws Exception {
        if (MenuTestData.MENU_4.getDate().isEqual(LocalDate.now())) {
            Vote update = new Vote(VoteTestData.VOTE_3);
            mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_4)
                    .with(TestUtil.userHttpBasic(UserTestData.USER)))
                    .andDo(print())
                    .andExpect(status().isOk());
            assertMatch(voteService.getForUserAndDate(UserTestData.USER_ID, VoteTestData.VOTE_3.getDate()), update);
        } else {
            mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_4)
                    .with(TestUtil.userHttpBasic(UserTestData.USER)))
                    .andDo(print())
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Test
    void updateVoteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MENUS_MAPPING + MenuTestData.MENU_ID_4)
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAllByDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "byDate?date=" + LocalDate.now())
                .with(TestUtil.userHttpBasic(UserTestData.ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(Collections.singletonList(VoteTestData.VOTE_3)));
    }
}