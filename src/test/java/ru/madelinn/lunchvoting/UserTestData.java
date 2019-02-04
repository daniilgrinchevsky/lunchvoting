package ru.madelinn.lunchvoting;

import org.springframework.test.web.servlet.ResultMatcher;
import ru.madelinn.lunchvoting.model.Role;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.web.json.JsonUtil;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static ru.madelinn.lunchvoting.RestaurantTestData.MAYBEER;
import static ru.madelinn.lunchvoting.RestaurantTestData.PENTHOUSE;
import static ru.madelinn.lunchvoting.model.AbstractBaseEntity.START_SEQ;
import static ru.madelinn.lunchvoting.web.json.JsonUtil.writeIgnoreProps;


public class UserTestData {

    public static final int USER_ID = START_SEQ+3;
    public static final int ADMIN_ID = START_SEQ + 4;

    public static final User USER = new User(USER_ID, "User", "user@gmail.com", "password", PENTHOUSE, Role.ROLE_USER);
    public static final User ADMIN = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", MAYBEER, Role.ROLE_ADMIN, Role.ROLE_USER );

    public static void assertMatch(User actual, User expected){
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "registered", "vote", "password");
    }

    public static void assertMatch(Iterable<User> actual, User... expected){
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<User> actual, Iterable<User> expected){
        assertThat(actual).usingElementComparatorIgnoringFields("registered", "vote", "password").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(User... expected) {
        return content().json(writeIgnoreProps(Arrays.asList(expected), "registered", "password"));
    }

    public static ResultMatcher contentJson(User expected) {
        return content().json(writeIgnoreProps(expected, "registered", "password"));
    }

    public static String jsonWithPassword(User user, String pass) {
        return JsonUtil.writeAdditionProps(user, "password", pass);
    }
}
