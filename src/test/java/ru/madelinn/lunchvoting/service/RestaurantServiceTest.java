package ru.madelinn.lunchvoting.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.madelinn.lunchvoting.DishTestData;
import ru.madelinn.lunchvoting.UserTestData;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.repository.JpaUtil;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.madelinn.lunchvoting.DishTestData.*;
import static ru.madelinn.lunchvoting.RestaurantTestData.assertMatch;
import static ru.madelinn.lunchvoting.RestaurantTestData.*;
import static ru.madelinn.lunchvoting.UserTestData.USER;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class RestaurantServiceTest {

    @Autowired
    protected RestaurantService service;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JpaUtil jpaUtil;

    @BeforeEach
    void setUp() throws Exception {
        cacheManager.getCache("restaurants").clear();
        jpaUtil.clearSecondLevelHibernateCache();
    }

    @Test
    void create() throws Exception {
        Restaurant newRestaurant = new Restaurant(null,"NewRestaurant");
        Restaurant created = service.create(new Restaurant(newRestaurant));
        newRestaurant.setId(created.getId());
        assertMatch(service.getAll(), LOUNGE, MAYBEER, newRestaurant, PENTHOUSE);
    }

    @Test
    void duplicateNameCreate() throws Exception {
        assertThrows(DataAccessException.class, () ->
        service.create(new Restaurant(null, "Penthouse")));
    }

    @Test
    void delete() throws Exception {
        service.delete(PENTHOUSE_ID);
        assertMatch(service.getAll(), LOUNGE, MAYBEER);
    }

    @Test
    void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
        service.delete(1));
    }

    @Test
    void get() throws Exception {
        Restaurant restaurant = service.get(PENTHOUSE_ID);
        assertMatch(restaurant, PENTHOUSE);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
        service.get(1));
    }

    @Test
    void getByName() throws Exception {
        Restaurant restaurant = service.getByName("Lounge");
        assertMatch(restaurant, LOUNGE);
    }

    @Test
    void getByNameNotFound() throws Exception {
        assertThrows(NotFoundException.class, () ->
        service.getByName("Unnamed"));
    }

    @Test
    void update() throws Exception {
        Restaurant updated = new Restaurant(PENTHOUSE);
        updated.setName("NewName");
        service.update(new Restaurant(updated));
        assertMatch(updated, service.get(PENTHOUSE_ID));
    }

    @Test
    void getAll() throws Exception {
        List<Restaurant> all = service.getAll();
        assertMatch(all, LOUNGE, MAYBEER, PENTHOUSE);
    }

    @Test
    void getWithDishes() throws Exception {
        Restaurant restaurant = service.getWithDishes(PENTHOUSE_ID);
        assertMatch(restaurant, PENTHOUSE);
        DishTestData.assertMatch(restaurant.getDishes(), TIRAMISU, CAESAR, NEAPOLITAN_PIZZA );
    }

    @Test
    void getWithDishesNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.getWithDishes(1));
    }

    @Test
    void getWithUsers() throws Exception {
        Restaurant restaurant = service.getWithUsers(PENTHOUSE_ID);
        assertMatch(restaurant, PENTHOUSE);
        UserTestData.assertMatch(restaurant.getUsers(), USER);
    }

    @Test
    void getWithUsersNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.getWithUsers(1));
    }

    @Test
    void getWithUsersAndDishes() throws Exception {
        Restaurant restaurant = service.getWithUsersAndDishes(PENTHOUSE_ID);
        assertMatch(restaurant, PENTHOUSE);
        UserTestData.assertMatch(restaurant.getUsers(), USER);
        DishTestData.assertMatch(restaurant.getDishes(), TIRAMISU, CAESAR, NEAPOLITAN_PIZZA);
    }

    @Test
    void getWithUsersAndDishesNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.getWithUsersAndDishes(1));
    }

    @Test
    void getVotes() throws Exception {
        Map<Integer, Integer> actual = new LinkedHashMap<>();
        actual.put(100000, 1);
        actual.put(100001, 1);
        actual.put(100002, 0);
        Map<Integer, Integer> expected = service.getVotes();
        assertThat(actual, is(expected));
    }
}
