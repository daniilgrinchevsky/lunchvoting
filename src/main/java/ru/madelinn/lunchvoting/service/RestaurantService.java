package ru.madelinn.lunchvoting.service;

import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.List;
import java.util.Map;

public interface RestaurantService {

    Restaurant create(Restaurant restaurant);

    Restaurant get(int id) throws NotFoundException;

    void delete (int id) throws NotFoundException;

    void update(Restaurant restaurant);

    Restaurant getByName(String name);

    List<Restaurant> getAll();

    Restaurant getWithUsers(int id) throws NotFoundException;

    Restaurant getWithDishes(int id) throws NotFoundException;

    Restaurant getWithUsersAndDishes(int id) throws NotFoundException;

    Map<Integer, Integer> getVotes();
}
