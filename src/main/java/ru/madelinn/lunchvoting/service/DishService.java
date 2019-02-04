package ru.madelinn.lunchvoting.service;

import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.List;

public interface DishService {

    Dish create(Dish dish, int restaurantId);

    void delete(int id, int restaurantId) throws NotFoundException;

    Dish get(int id, int restaurantId) throws NotFoundException;

    void update(Dish dish, int restaurantId) throws NotFoundException;

    List<Dish> getAll(int restaurantId);

    Dish getWithRestaurant(int id, int restaurantId) throws NotFoundException;
}
