package ru.madelinn.lunchvoting.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.madelinn.lunchvoting.TestUtil;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.service.UserService;
import ru.madelinn.lunchvoting.to.UserTo;
import ru.madelinn.lunchvoting.util.Util;
import ru.madelinn.lunchvoting.util.exception.ErrorType;
import ru.madelinn.lunchvoting.web.json.JsonUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.madelinn.lunchvoting.RestaurantTestData.MAYBEER;
import static ru.madelinn.lunchvoting.RestaurantTestData.MAYBEER_ID;
import static ru.madelinn.lunchvoting.TestUtil.readFromJson;
import static ru.madelinn.lunchvoting.TestUtil.userHttpBasic;
import static ru.madelinn.lunchvoting.UserTestData.*;
import static ru.madelinn.lunchvoting.web.ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL;
import static ru.madelinn.lunchvoting.web.UserRestController.REST_URL;


public class UserRestControllerTest extends AbstractControllerTest {

    @Autowired
    UserService userService;


    @Test
    void testGet() throws Exception {
        TestUtil.print(
                mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER))
        );
    }

    @Test
    void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        assertMatch(userService.getAll(), ADMIN);
    }

    @Test
    void testRegister() throws Exception {
        UserTo createdTo = new UserTo(null, "newUser", "newemail@gmail.com", "newPassword", MAYBEER);
        ResultActions action = mockMvc.perform(post(REST_URL+"/register").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(createdTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        User returned = readFromJson(action, User.class);

        User created = Util.createNewFromTo(createdTo);
        created.setId(returned.getId());

        assertMatch(returned, created);
        assertMatch(userService.getByEmail("newemail@gmail.com"), created);
    }

    @Test
    void testUpdate() throws Exception {
        UserTo updatedTo = new UserTo(null, "newUser", "newemail@gmail.com", "newPassword", MAYBEER);

        mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(USER))
        .content(JsonUtil.writeValue(updatedTo)))
        .andDo(print())
        .andExpect(status().isNoContent());

        assertMatch(userService.getByEmail("newemail@gmail.com"), Util.updateFromTo(new User(USER), updatedTo));
    }

    @Test
    void testUpdateInvalid() throws Exception {
        UserTo updatedTo = new UserTo(null, null, "password", null, MAYBEER);

        mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(USER))
        .content(JsonUtil.writeValue(updatedTo)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(errorType(ErrorType.VALIDATION_ERROR))
        .andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testDuplicate() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "admin@gmail.com", "newPassword", MAYBEER);

        mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(USER))
        .content(JsonUtil.writeValue(updatedTo)))
        .andExpect(status().isConflict())
        .andExpect(errorType(ErrorType.VALIDATION_ERROR))
        .andExpect(jsonMessage("$.details", EXCEPTION_DUPLICATE_EMAIL))
        .andDo(print());
    }

    @Test
    void testVote() throws Exception {
        UserTo updatedTo = new UserTo(USER_ID, "User", "user@gmail.com", "password", MAYBEER);
        mockMvc.perform(put(REST_URL + "/vote/" + MAYBEER_ID).contentType(MediaType.APPLICATION_JSON)
        .with(userHttpBasic(USER))
        .content(JsonUtil.writeValue(updatedTo)))
        .andDo(print())
        .andExpect(status().isNoContent());

        assertMatch(userService.get(USER_ID), Util.updateFromTo(new User(USER), updatedTo));

    }

}
