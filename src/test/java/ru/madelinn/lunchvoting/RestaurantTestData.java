package ru.madelinn.lunchvoting;


import org.springframework.test.web.servlet.ResultMatcher;
import ru.madelinn.lunchvoting.model.Restaurant;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static ru.madelinn.lunchvoting.model.AbstractBaseEntity.START_SEQ;
import static ru.madelinn.lunchvoting.web.json.JsonUtil.writeIgnoreProps;

public class RestaurantTestData {

    public static final int PENTHOUSE_ID = START_SEQ;
    public static final int MAYBEER_ID = START_SEQ + 1;
    public static final int LOUNGE_ID = START_SEQ + 2;

    public static final Restaurant PENTHOUSE = new Restaurant(PENTHOUSE_ID, "Penthouse");
    public static final Restaurant MAYBEER = new Restaurant(MAYBEER_ID, "Maybeer");
    public static final Restaurant LOUNGE = new Restaurant(LOUNGE_ID, "Lounge");

    public static void assertMatch(Restaurant actual, Restaurant expected){
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "users", "dishes");
    }

    public static void assertMatch(Iterable<Restaurant> actual, Restaurant... expected){
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Restaurant> actual, Iterable<Restaurant> expected){
        assertThat(actual).usingElementComparatorIgnoringFields("users", "dishes").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Restaurant... expected) {
        return content().json(writeIgnoreProps(Arrays.asList(expected), "dishes", "users"));
    }
}
