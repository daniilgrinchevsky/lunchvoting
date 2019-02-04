package ru.madelinn.lunchvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.madelinn.lunchvoting.DishTestData;
import ru.madelinn.lunchvoting.TestUtil;
import ru.madelinn.lunchvoting.service.DishService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.madelinn.lunchvoting.DishTestData.*;
import static ru.madelinn.lunchvoting.RestaurantTestData.PENTHOUSE_ID;
import static ru.madelinn.lunchvoting.TestUtil.contentJson;
import static ru.madelinn.lunchvoting.TestUtil.userHttpBasic;
import static ru.madelinn.lunchvoting.UserTestData.USER;
import static ru.madelinn.lunchvoting.web.DishRestController.REST_URL;

public class DishRestControllerTest extends AbstractControllerTest {

    @Autowired
    DishService dishService;

    @Test
    void testGet() throws Exception {
        TestUtil.print(
                mockMvc.perform(get(REST_URL + "/"+ PENTHOUSE_ID + "/" + TIRAMISU_ID)
                        .with(userHttpBasic(USER)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(contentJson(TIRAMISU)));
    }

    @Test
    void testGetAll() throws Exception {
        TestUtil.print(
                mockMvc.perform(get(REST_URL + "/" + PENTHOUSE_ID)
                        .with(userHttpBasic(USER)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(DishTestData.contentJson(TIRAMISU, CAESAR, NEAPOLITAN_PIZZA)));
    }

    @Test
    void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + "/" + PENTHOUSE_ID + "/" + HAMBURGER_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isUnprocessableEntity());
    }
}
