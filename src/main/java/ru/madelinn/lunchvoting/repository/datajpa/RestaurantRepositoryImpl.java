package ru.madelinn.lunchvoting.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.madelinn.lunchvoting.model.Restaurant;
import ru.madelinn.lunchvoting.repository.RestaurantRepository;

import java.util.List;

@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private static final Sort SORT_NAME = new Sort(Sort.Direction.ASC,"name");

    @Autowired
    private CrudRestaurantRepository crudRepository;

    @Override
    public Restaurant save(Restaurant restaurant) {
        return crudRepository.save(restaurant);
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.delete(id) != 0;
    }

    @Override
    public Restaurant get(int id) {
        return crudRepository.findById(id).orElse(null);
    }

    @Override
    public Restaurant getByName(String name) {
        return crudRepository.getByName(name);
    }

    @Override
    public List<Restaurant> getAll() {
        return crudRepository.findAll(SORT_NAME);
    }

    @Override
    public Restaurant getWithUsers(int id) {
        return crudRepository.getWithUsers(id);
    }

    @Override
    public Restaurant getWithDishes(int id) {
        return crudRepository.getWithDishes(id);
    }

    @Override
    public Restaurant getWithUsersAndDishes(int id) {
        return crudRepository.getWithUsersAndDishes(id);
    }
}
