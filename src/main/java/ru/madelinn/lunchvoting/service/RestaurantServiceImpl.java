package ru.madelinn.lunchvoting.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.repository.RestaurantRepository;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static ru.madelinn.lunchvoting.util.Util.checkNotFound;
import static ru.madelinn.lunchvoting.util.Util.checkNotFoundWithId;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository repository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository repository){
        this.repository = repository;
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    @Override
    public Restaurant create(Restaurant restaurant) {
        Assert.notNull(restaurant, "Restaurant must not be null");
        return repository.save(restaurant);
    }

    @Override
    public Restaurant get(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.get(id), id);
    }

    @Override
    public Restaurant getByName(String name) {
        Assert.notNull(name, "Name must not be null");
        return checkNotFound(repository.getByName(name), "name=" + name);
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    @Override
    public void delete(int id) throws NotFoundException {
        checkNotFoundWithId(repository.delete(id), id);
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    @Override
    public void update(Restaurant restaurant) {
        Assert.notNull(restaurant, "Restaurant must not be null");
        checkNotFoundWithId(repository.save(restaurant), restaurant.getId());
    }

    @Cacheable("restaurants")
    @Override
    public List<Restaurant> getAll() {
        return repository.getAll();
    }

    @Override
    public Restaurant getWithUsers(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.getWithUsers(id), id);
    }

    @Override
    public Restaurant getWithDishes(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.getWithDishes(id), id);
    }

    @Override
    public Restaurant getWithUsersAndDishes(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.getWithUsersAndDishes(id), id);
    }

    @Override
    public Map<Integer, Integer> getVotes() {
        Map<Integer, Integer> votes = new HashMap<>();
        List<Restaurant> restaurants = getAll();
        List<Restaurant> newRestaurants = new ArrayList<>();
        for(Restaurant r : restaurants){
            newRestaurants.add(getWithUsers(r.getId()));
        }
        for(Restaurant r : newRestaurants){
            votes.put(r.getId(), r.getUsers().size());
        }
        Map<Integer, Integer> sorted = votes.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        return sorted;
    }
}
