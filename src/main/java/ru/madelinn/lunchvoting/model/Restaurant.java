package ru.madelinn.lunchvoting.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Set;

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "restaurants", uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "restaurant_unique_name_idx")})
public class Restaurant extends AbstractBaseEntity {

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vote")
    protected Set<User> users;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "restaurant")
    @JsonManagedReference
    protected Set<Dish> dishes;

    public Restaurant (){

    }

    public Restaurant(Restaurant r){
        this(r.getId(), r.getName());
    }

    public Restaurant(String name, Set<Dish> dishes){
        this(null, name, dishes);
    }

    public Restaurant(Integer id, String name, Set<Dish> dishes){
        super(id, name);
        this.dishes = dishes;
    }

    public Restaurant(Integer id, String name){
        super(id, name);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<Dish> dishes) {
        this.dishes = dishes;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", dishes=" + dishes +
                '}';
    }
}
