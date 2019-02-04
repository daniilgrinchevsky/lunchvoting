package ru.madelinn.lunchvoting.service;

import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.madelinn.lunchvoting.AuthorizedUser;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.repository.UserRepository;
import ru.madelinn.lunchvoting.to.UserTo;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static ru.madelinn.lunchvoting.util.Util.*;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public User create(User user) {
        Assert.notNull(user, "User must not be null");
        return repository.save(prepareToSave(user, passwordEncoder));
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public void delete(int id) throws NotFoundException {
        checkNotFoundWithId(repository.delete(id),id);
    }

    @Override
    public User get(int id) throws NotFoundException {
        return checkNotFoundWithId(repository.get(id), id);
    }

    @Override
    public User getByEmail(String email) throws NotFoundException {
        Assert.notNull(email, "Email must not be null");
        return checkNotFound(repository.getByEmail(email), "email=" + email);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public void update(User user) {
        Assert.notNull(user, "User must not be null");
        checkNotFoundWithId(repository.save(prepareToSave(user, passwordEncoder)),user.getId());
    }
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    @Override
    public void update(UserTo userTo) {
        User user = updateFromTo(get(userTo.getId()), userTo);
        repository.save(prepareToSave(user, passwordEncoder));
    }

    @Cacheable("users")
    @Override
    public List<User> getAll() {
        return repository.getAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public void vote(int userId, int restaurantId) throws NotFoundException, UnsupportedOperationException {
        Assert.notNull(userId, "UserId must not be null");
        checkNotFoundWithId(repository.get(userId), userId);
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(DateTimeUtils.currentTimeMillis()),ZoneId.systemDefault());
        if(currentTime.getHour() < 11) {
            repository.vote(userId, restaurantId);
        }
        else {throw new UnsupportedOperationException("Vote can't be changed");}
    }

    @CacheEvict(value = "users", allEntries = true)
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void eraseVote() {
        repository.eraseVote();
    }

    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.getByEmail(email.trim().toLowerCase());
        if(user == null)
            throw new UsernameNotFoundException("User " + email + "is not found");
        return new AuthorizedUser(user);
    }

}
