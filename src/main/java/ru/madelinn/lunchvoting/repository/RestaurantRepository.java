package ru.madelinn.lunchvoting.repository;

import ru.madelinn.lunchvoting.model.Restaurant;

import java.util.List;

public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    boolean delete(int id);

    Restaurant get(int id);

    Restaurant getByName(String name);

    List<Restaurant> getAll();

    default Restaurant getWithUsers(int id) {throw new UnsupportedOperationException();}

    default Restaurant getWithDishes(int id) {throw new UnsupportedOperationException();}

    default Restaurant getWithUsersAndDishes(int id) {throw new UnsupportedOperationException();}
}
