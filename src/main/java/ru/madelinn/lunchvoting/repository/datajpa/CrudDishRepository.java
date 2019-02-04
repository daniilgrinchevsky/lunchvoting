package ru.madelinn.lunchvoting.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.madelinn.lunchvoting.model.Dish;

import java.util.List;
import java.util.Optional;

public interface CrudDishRepository extends JpaRepository<Dish, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restaurantId")
    int delete(@Param("id")int id,@Param("restaurantId") int restaurantId);

    @Override
    @Transactional
    Dish save(Dish dish);

    @Override
    Optional<Dish> findById(Integer integer);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=:restaurantId ORDER BY d.name")
    List<Dish> getAll(@Param("restaurantId") int restaurantId);

    @Query("SELECT d FROM Dish d JOIN FETCH d.restaurant WHERE d.id = ?1 AND d.restaurant.id = ?2")
    Dish getWithRestaurant(int id, int restaurantId);
}
