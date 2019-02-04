package ru.madelinn.lunchvoting.web.json;

import org.junit.jupiter.api.Test;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.madelinn.lunchvoting.DishTestData.PENTHOUSE_DISHES;
import static ru.madelinn.lunchvoting.DishTestData.assertMatch;
import static ru.madelinn.lunchvoting.UserTestData.USER;
import static ru.madelinn.lunchvoting.UserTestData.assertMatch;
import static ru.madelinn.lunchvoting.UserTestData.jsonWithPassword;

public class JsonUtilTest {

    @Test
    void testReadWriteValue() throws Exception {
        String json = JsonUtil.writeValue(USER);
        System.out.println(json);
        User user = JsonUtil.readValue(json, User.class);
        assertMatch(user, USER);
    }

    @Test
    void testReadWriteValues() throws Exception {
        String json = JsonUtil.writeValue(PENTHOUSE_DISHES);
        System.out.println(json);
        List<Dish> dishes = JsonUtil.readValues(json, Dish.class);
        assertMatch(dishes, PENTHOUSE_DISHES);
    }

    @Test
    void testWriteOnlyAccess() throws Exception {
        String json = JsonUtil.writeValue(USER);
        System.out.println(json);
        assertThat(json, not(containsString("password")));
        String jsonWithPass = jsonWithPassword(USER, "newPass");
        System.out.println(jsonWithPass);
        User user = JsonUtil.readValue(jsonWithPass, User.class);
        assertEquals(user.getPassword(), "newPass");
    }


}
