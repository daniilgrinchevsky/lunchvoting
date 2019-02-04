package ru.madelinn.lunchvoting.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.repository.DishRepository;

import java.util.List;

@Repository
public class DishRepositoryImpl implements DishRepository {

    @Autowired
    private CrudDishRepository crudDishRepository;

    @Autowired
    private CrudRestaurantRepository crudRestaurantRepository;

    @Override
    @Transactional
    public Dish save(Dish dish, int restaurantId) {
        if(!dish.isNew() && get(dish.getId(), restaurantId) == null)
            return null;
        dish.setRestaurant(crudRestaurantRepository.getOne(restaurantId));
        return crudDishRepository.save(dish);
    }

    @Override
    public boolean delete(int id, int restaurantId) {
        return crudDishRepository.delete(id, restaurantId) != 0;
    }

    @Override
    public Dish get(int id, int restaurantId) {
        return crudDishRepository.findById(id).filter(dish -> dish.getRestaurant().getId() == restaurantId).orElse(null);
    }

    @Override
    public List<Dish> getAll(int restaurantId) {
        return crudDishRepository.getAll(restaurantId);
    }

    @Override
    public Dish getWithRestaurant(int id, int restaurantId) {
        return crudDishRepository.getWithRestaurant(id, restaurantId);
    }
}
