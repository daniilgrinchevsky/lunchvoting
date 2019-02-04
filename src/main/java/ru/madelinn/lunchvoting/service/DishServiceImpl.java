package ru.madelinn.lunchvoting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.madelinn.lunchvoting.model.Dish;
import ru.madelinn.lunchvoting.repository.DishRepository;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.List;

import static ru.madelinn.lunchvoting.util.Util.checkNotFoundWithId;

@Service
public class DishServiceImpl implements DishService{

    private final DishRepository  repository;

    @Autowired
    public DishServiceImpl(DishRepository repository){
        this.repository = repository;
    }

    @Override
    public Dish create(Dish dish, int restaurantId) {
        Assert.notNull(dish, "Dish must not be null");
        return repository.save(dish, restaurantId);
    }

    @Override
    public void delete(int id, int restaurantId) throws NotFoundException {
        checkNotFoundWithId(repository.delete(id, restaurantId),id);
    }

    @Override
    public Dish get(int id, int restaurantId) throws NotFoundException {
        return checkNotFoundWithId(repository.get(id, restaurantId), id);
    }

    @Override
    public void update(Dish dish, int restaurantId) throws NotFoundException {
        checkNotFoundWithId(repository.save(dish, restaurantId), dish.getId());
    }

    @Override
    public List<Dish> getAll(int restaurantId) {
        return repository.getAll(restaurantId);
    }

    @Override
    public Dish getWithRestaurant(int id, int restaurantId) throws NotFoundException {
        return checkNotFoundWithId(repository.getWithRestaurant(id, restaurantId), id);
    }
}
