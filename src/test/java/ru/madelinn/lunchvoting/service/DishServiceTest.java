package ru.madelinn.lunchvoting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.madelinn.lunchvoting.RestaurantTestData;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.madelinn.lunchvoting.DishTestData.assertMatch;
import static ru.madelinn.lunchvoting.DishTestData.*;
import static ru.madelinn.lunchvoting.RestaurantTestData.*;

@SpringJUnitConfig(locations = {
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class DishServiceTest {

    @Autowired
    protected DishService service;

    @Test
    void create() throws Exception {
        Dish created = new Dish(null, "CreatedDish", 100);
        service.create(created, PENTHOUSE_ID);
        assertMatch(service.getAll(PENTHOUSE_ID), CAESAR, created, NEAPOLITAN_PIZZA, TIRAMISU);
    }

    @Test
    void delete() throws Exception {
        service.delete(TIRAMISU_ID, PENTHOUSE_ID);
        assertMatch(service.getAll(PENTHOUSE_ID), CAESAR, NEAPOLITAN_PIZZA);
    }

    @Test
    void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.delete(TIRAMISU_ID, 1));
    }

    @Test
    void get() throws Exception {
        Dish dish = service.get(TOM_YUM_ID, LOUNGE_ID);
        assertMatch(dish, TOM_YUM);
    }

    @Test
    void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.get(NEAPOLITAN_PIZZA_ID, MAYBEER_ID));
    }

    @Test
    void update() throws Exception {
        Dish updated = new Dish(TIRAMISU);
        updated.setName("Updated");
        updated.setPrice(666);
        service.update(new Dish(updated),PENTHOUSE_ID);
        assertMatch(updated, service.get(TIRAMISU_ID, PENTHOUSE_ID));
    }

    @Test
    void updateNotFound() throws Exception {
        Dish updated = new Dish(TIRAMISU);
        updated.setName("Updated");
        updated.setPrice(666);
        assertThrows(NotFoundException.class, ()->
        service.update(updated,LOUNGE_ID));
    }

    @Test
    void getAll() throws Exception {
        List<Dish> all = service.getAll(MAYBEER_ID);
        assertMatch(all, FISH_AND_CHIPS, GREEK_SALAD, HAMBURGER);
    }

    @Test
    void getWithRestaurant() throws Exception {
        Dish dish = service.getWithRestaurant(TIRAMISU_ID, PENTHOUSE_ID);
        assertMatch(dish, TIRAMISU);
        RestaurantTestData.assertMatch(dish.getRestaurant(), PENTHOUSE);
    }

    @Test
    void getWithRestaurantNotFound() throws Exception {
        assertThrows(NotFoundException.class, ()->
        service.getWithRestaurant(LASAGNA_ID, PENTHOUSE_ID));
    }


}
