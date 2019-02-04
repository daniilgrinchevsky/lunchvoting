package ru.madelinn.lunchvoting.repository;

import ru.madelinn.lunchvoting.model.User;

import java.util.List;

public interface UserRepository {

    User save(User user);

    boolean delete(int id);

    User get(int id);

    User getByEmail(String email);

    List<User> getAll();

    void vote(int userId, int restaurantId);

    void eraseVote();
}
