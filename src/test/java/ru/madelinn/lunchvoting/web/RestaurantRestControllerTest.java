package ru.madelinn.lunchvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.madelinn.lunchvoting.RestaurantTestData;
import ru.madelinn.lunchvoting.TestUtil;
import ru.madelinn.lunchvoting.service.RestaurantService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.madelinn.lunchvoting.RestaurantTestData.*;
import static ru.madelinn.lunchvoting.TestUtil.contentJson;
import static ru.madelinn.lunchvoting.TestUtil.userHttpBasic;
import static ru.madelinn.lunchvoting.UserTestData.USER;
import static ru.madelinn.lunchvoting.web.RestaurantRestController.REST_URL;

public class  RestaurantRestControllerTest extends AbstractControllerTest {

    @Autowired
    RestaurantService restaurantService;

    @Test
    void testGet() throws Exception {
        TestUtil.print(
                mockMvc.perform(get(REST_URL + "/" + PENTHOUSE_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(PENTHOUSE))
        );
    }

    @Test
    void testGetAll() throws Exception {
        TestUtil.print(
                mockMvc.perform(get(REST_URL)
                        .with(userHttpBasic(USER)))
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(RestaurantTestData.contentJson(PENTHOUSE, MAYBEER, LOUNGE)));
    }

    @Test
    void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + "/1")
                .with(userHttpBasic(USER)))
                .andExpect(status().isUnprocessableEntity());
    }
}
