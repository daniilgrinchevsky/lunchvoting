package ru.madelinn.lunchvoting.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.repository.UserRepository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private static final Sort SORT_NAME_EMAIL = new Sort(Sort.Direction.ASC, "name", "email");

    @Autowired
    private CrudUserRepository crudRepository;

    @Override
    public User save(User user) {
        return crudRepository.save(user);
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.delete(id) != 0;
    }

    @Override
    public User get(int id) {
        return crudRepository.findById(id).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return crudRepository.getByEmail(email);
    }

    @Override
    public List<User> getAll() {
        return crudRepository.findAll(SORT_NAME_EMAIL);
    }

    @Override
    public void vote(int userId, int restaurantId) {
        crudRepository.vote(userId, restaurantId);
    }

    @Override
    public void eraseVote() {
        crudRepository.eraseVote();
    }
}
