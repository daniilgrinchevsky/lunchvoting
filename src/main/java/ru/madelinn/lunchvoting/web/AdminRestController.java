package ru.madelinn.lunchvoting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.madelinn.lunchvoting.View;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.service.DishService;
import ru.madelinn.lunchvoting.service.RestaurantService;

import java.net.URI;
import java.util.Map;

import static ru.madelinn.lunchvoting.util.Util.assureIdConsistent;
import static ru.madelinn.lunchvoting.util.Util.checkNew;

@RestController
@RequestMapping(AdminRestController.REST_URL)
public class AdminRestController {

    static final String REST_URL = "/rest/admin";
    private static final Logger log = LoggerFactory.getLogger(AdminRestController.class);

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private DishService dishService;


    @DeleteMapping("/restaurants/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        log.info("delete restaurant {}", id);
        restaurantService.delete(id);
    }

    @PutMapping(value = "/restaurants/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Restaurant restaurant, @PathVariable("id") int id) {
        assureIdConsistent(restaurant, id);
        log.info("update {} with id={}", restaurant, id);
        restaurantService.update(restaurant);
    }

    @PostMapping(value = "/restaurants" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Restaurant> createWithLocation(@Validated(View.Web.class) @RequestBody Restaurant restaurant){
        log.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = restaurantService.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("rest/restaurants/" + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/votes")
    public Map<Integer, Integer> getVotes(){
        log.info("get votes");
        return restaurantService.getVotes();
    }

    @DeleteMapping("/dishes/{restaurantId}/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id,@PathVariable("restaurantId") int restaurantId){
        log.info("delete dish {} for restaurant {}", id, restaurantId);
        dishService.delete(id, restaurantId);
    }

    @PutMapping("/dishes/{restaurantId}/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Dish dish, @PathVariable("restaurantId") int restaurantId, @PathVariable("id") int id){
        assureIdConsistent(dish, id);
        log.info("update dish {} with id={} for restaurant {}", dish, id, restaurantId);
        dishService.update(dish, restaurantId);
    }

    @PostMapping(value = "/dishes/{restaurantId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Dish> createWithLocation(@Validated(View.Web.class) @RequestBody Dish dish, @PathVariable("restaurantId") int restaurantId){
        log.info("create dish {} for restaurant {}", dish, restaurantId);
        checkNew(dish);
        Dish created = dishService.create(dish, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("rest/dishes" + "/{restaurantId}" + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}
