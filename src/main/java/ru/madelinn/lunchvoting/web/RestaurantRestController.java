package ru.madelinn.lunchvoting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.service.RestaurantService;

import java.util.List;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {

    static final String REST_URL = "/rest/restaurants";
    private static final Logger log = LoggerFactory.getLogger(RestaurantRestController.class);

    @Autowired
    private RestaurantService service;

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable("id") int id){
        log.info("get restaurant {}", id);
        return service.getWithDishes(id);
    }

    @GetMapping
    public List<Restaurant> getAll(){
        log.info("get all restaurants");
        return service.getAll();
    }

}
