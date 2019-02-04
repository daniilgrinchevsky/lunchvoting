package ru.madelinn.lunchvoting.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.madelinn.lunchvoting.model.User;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface CrudUserRepository extends JpaRepository<User, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id=:id")
    int delete(@Param("id")int id);

    @Override
    @Transactional
    User save(User user);

    @Override
    Optional<User> findById(Integer id);

    @Override
    List<User> findAll(Sort sort);

    User getByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.vote.id=:restaurantId WHERE u.id=:userId")
    void vote(@Param("userId")int userId, @Param("restaurantId") int restaurantId);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.vote=NULL")
    void eraseVote();
}
