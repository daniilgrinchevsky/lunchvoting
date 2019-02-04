package ru.madelinn.lunchvoting.service;

import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.to.UserTo;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.util.List;

public interface UserService {

    User create(User user);

    void delete (int id) throws NotFoundException;

    User get(int id) throws NotFoundException;

    User getByEmail(String email) throws NotFoundException;

    void update(User user);

    void update(UserTo userTo);

    List<User> getAll();

    void vote (int userId, int restaurantId) throws  NotFoundException, UnsupportedOperationException;

    void eraseVote();
}
