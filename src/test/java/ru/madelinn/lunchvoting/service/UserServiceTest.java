package ru.madelinn.lunchvoting.service;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.model.Role;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.repository.JpaUtil;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.madelinn.lunchvoting.RestaurantTestData.*;
import static ru.madelinn.lunchvoting.UserTestData.assertMatch;
import static ru.madelinn.lunchvoting.UserTestData.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class UserServiceTest {


    @Autowired
    protected UserService service;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JpaUtil jpaUtil;

    @BeforeEach
    void setUp() throws Exception {
        cacheManager.getCache("users").clear();
        jpaUtil.clearSecondLevelHibernateCache();
    }

    @Test
    void create() throws Exception {
        User newUser = new User(null, "New user", "newuser@gmail.com", "newPassword", new Date(), null, Collections.singleton(Role.ROLE_USER));
        User created = service.create(new User(newUser));
        newUser.setId(created.getId());
        assertMatch(service.getAll(), ADMIN, newUser, USER);
    }

    @Test
    void duplicateEmailCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
        service.create(new User(null, "Duplicate", "user@gmail.com", "newPassword", new Date(), null, Collections.singleton(Role.ROLE_USER))));
    }

    @Test
    void delete() throws Exception {
        service.delete(USER_ID);
        assertMatch(service.getAll(), ADMIN);
    }

    @Test
    void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
        service.delete(1));
    }

    @Test
    void get() throws Exception {
        User user = service.get(USER_ID);
        assertMatch(user, USER);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
        service.get(1));
    }

    @Test
    void getByEmail() throws Exception {
        User user = service.getByEmail("admin@gmail.com");
        assertMatch(user, ADMIN);
    }

    @Test
    void getByEmailNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
        service.getByEmail("newEmail@gmail.com"));
    }

    @Test
    void update() throws Exception {
        User updated = new User(USER);
        updated.setName("Updated");
        updated.setVote(new Restaurant(PENTHOUSE));
        updated.setRoles(Collections.singleton(Role.ROLE_ADMIN));
        service.update(new User(updated));
        assertMatch(updated, service.get(USER_ID));
    }

    @Test
    void getAll() throws Exception {
        List<User> all = service.getAll();
        assertMatch(all, ADMIN, USER);
    }

    @Test
    void vote() throws Exception {
        LocalDateTime beforeEleven = new LocalDateTime().withHourOfDay(8);
        DateTimeUtils.setCurrentMillisFixed(beforeEleven.toDateTime().getMillis());
        service.vote(USER_ID, LOUNGE_ID);
        User updated = new User(USER);
        updated.setVote(new Restaurant(LOUNGE));
        assertMatch(service.get(USER_ID), updated);
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    void voteAfterEleven() throws Exception {
        LocalDateTime afterEleven = new LocalDateTime().withHourOfDay(14);
        DateTimeUtils.setCurrentMillisFixed(afterEleven.toDateTime().getMillis());
        assertThrows(UnsupportedOperationException.class, ()->
        service.vote(USER_ID, LOUNGE_ID));
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    void voteUserNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.vote((1), LOUNGE_ID));

    }

    @Test
    void eraseVoteTest() throws Exception {
        assertEquals((int)service.get(USER_ID).getVote().getId(), PENTHOUSE_ID);
        service.eraseVote();
        assertThrows(NullPointerException.class, ()->
        service.get(USER_ID).getVote().getId());
    }

}
