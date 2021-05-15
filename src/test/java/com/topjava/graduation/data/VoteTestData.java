package com.topjava.graduation.data;

import com.topjava.graduation.TestUtil;
import org.springframework.test.web.servlet.ResultMatcher;
import com.topjava.graduation.model.Vote;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.topjava.graduation.model.AbstractBaseEntity.START_SEQ;

public class VoteTestData {
    public static final int VOTE_ID_1 = START_SEQ + 16;
    public static final int VOTE_ID_2 = START_SEQ + 17;
    public static final int VOTE_ID_3 = START_SEQ + 18;

    public static final Vote VOTE_1 = new Vote(VOTE_ID_1, LocalDate.of(2021, 4, 23));
    public static final Vote VOTE_2 = new Vote(VOTE_ID_2, LocalDate.of(2021, 4, 23));
    public static final Vote VOTE_3 = new Vote(VOTE_ID_3, LocalDate.now());

    public static void assertMatch(Vote actual, Vote expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "user", "menu");
    }

    public static void assertMatch(Iterable<Vote> actual, Vote... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<Vote> actual, Iterable<Vote> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("user", "menu").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Vote... expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, Vote.class), List.of(expected));
    }

    public static ResultMatcher contentJson(List<Vote> expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, Vote.class), expected);
    }

    public static ResultMatcher contentJson(Vote expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, Vote.class), expected);
    }
}