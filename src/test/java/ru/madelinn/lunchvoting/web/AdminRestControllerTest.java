package ru.madelinn.lunchvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.madelinn.lunchvoting.TestUtil;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.service.DishService;
import ru.madelinn.lunchvoting.service.RestaurantService;
import ru.madelinn.lunchvoting.util.exception.ErrorType;
import ru.madelinn.lunchvoting.web.json.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.madelinn.lunchvoting.DishTestData.assertMatch;
import static ru.madelinn.lunchvoting.DishTestData.*;
import static ru.madelinn.lunchvoting.RestaurantTestData.assertMatch;
import static ru.madelinn.lunchvoting.RestaurantTestData.*;
import static ru.madelinn.lunchvoting.TestUtil.contentJson;
import static ru.madelinn.lunchvoting.TestUtil.*;
import static ru.madelinn.lunchvoting.UserTestData.ADMIN;
import static ru.madelinn.lunchvoting.web.AdminRestController.REST_URL;
import static ru.madelinn.lunchvoting.web.ExceptionInfoHandler.EXCEPTION_DUPLICATE_NAME;

public class AdminRestControllerTest extends AbstractControllerTest {

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    DishService dishService;

    @Test
    void testDeleteRestaurant() throws Exception {
        mockMvc.perform(delete(REST_URL + "/restaurants/" + PENTHOUSE_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(restaurantService.getAll(), LOUNGE, MAYBEER);
    }

    @Test
    void testUpdateRestaurant() throws Exception {
        Restaurant updated = new Restaurant(PENTHOUSE);
        updated.setName("Updated");
        mockMvc.perform(put(REST_URL + "/restaurants/" + PENTHOUSE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());

        assertMatch(restaurantService.get(PENTHOUSE_ID), updated);
    }

    @Test
    void testCreateRestaurant() throws Exception {
        Restaurant created = new Restaurant(null, "Created");

        ResultActions action = mockMvc.perform(post(REST_URL + "/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(ADMIN)));

        Restaurant returned = readFromJson(action, Restaurant.class);
        created.setId(returned.getId());

        assertMatch(returned, created);
        assertMatch(restaurantService.getAll(), created, LOUNGE, MAYBEER, PENTHOUSE);
    }

    @Test
    void testGetVotes() throws Exception {
        Map<Integer, Integer> expected = new LinkedHashMap<>();
        expected.put(PENTHOUSE_ID, 1);
        expected.put(MAYBEER_ID, 1);
        expected.put(LOUNGE_ID, 0);
        TestUtil.print(
                mockMvc.perform(get(REST_URL + "/votes")
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(expected))
        );
    }

    @Test
    void testDeleteDish() throws Exception {
        mockMvc.perform(delete(REST_URL + "/dishes/" + PENTHOUSE_ID + "/100005")
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(dishService.getAll(100000), CAESAR, NEAPOLITAN_PIZZA);
    }

    @Test
    void testUpdateDish() throws Exception {
        Dish updated = new Dish(TIRAMISU);
        updated.setName("Updated");
        mockMvc.perform(put(REST_URL + "/dishes/" + PENTHOUSE_ID + "/100005")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isNoContent());
        assertMatch(dishService.get(100005,100000), updated);
    }

    @Test
    void testCreateDish() throws Exception {
        Dish created = new Dish(null, "Created", 25);

        ResultActions action = mockMvc.perform(post(REST_URL + "/dishes/" + PENTHOUSE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(ADMIN)));

        Dish returned = readFromJson(action, Dish.class);
        created.setId(returned.getId());

        assertMatch(returned, created);
        assertMatch(dishService.getAll(100000), CAESAR, created, NEAPOLITAN_PIZZA, TIRAMISU);
    }

    @Test
    void testUpdateRestaurantInvalid() throws Exception {
        Restaurant updated = new Restaurant(100000, null);
        mockMvc.perform(put(REST_URL + "/restaurants/100000").contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testDuplicateRestaurant() throws Exception {
        Restaurant updated = new Restaurant(null, "Penthouse");

        mockMvc.perform(post(REST_URL + "/restaurants").contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isConflict())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR))
                .andExpect(jsonMessage("$.details", EXCEPTION_DUPLICATE_NAME))
                .andDo(print());
    }

    @Test
    void testUpdateDishInvalid() throws Exception {
        Dish updated = new Dish(null,null, null);
        mockMvc.perform(put(REST_URL + "/dishes/100000/100005").contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(ErrorType.VALIDATION_ERROR))
                .andDo(print());
    }

}
