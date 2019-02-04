package ru.madelinn.lunchvoting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.service.DishService;

import java.util.List;


@RestController
@RequestMapping(value = DishRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DishRestController {

    static final String REST_URL = "/rest/dishes";
    private static final Logger log = LoggerFactory.getLogger(DishRestController.class);

    @Autowired
    private DishService service;

    @GetMapping("/{restaurantId}/{id}")
    public Dish get(@PathVariable("id") int id,@PathVariable("restaurantId") int restaurantId){
        log.info("get dish {} for restaurant {}", id, restaurantId);
        return service.get(id, restaurantId);
    }

    @GetMapping("/{restaurantId}")
    public List<Dish> getAll(@PathVariable("restaurantId") int restaurantId){
        log.info("get all dishes for restaurant {}", restaurantId);
        return service.getAll(restaurantId);
    }

}
