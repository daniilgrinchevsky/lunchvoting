package ru.madelinn.lunchvoting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.SafeHtml;
import ru.madelinn.lunchvoting.View;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_unique_email_idx")})
public class User extends AbstractBaseEntity{

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @Size(max = 50)
    @SafeHtml(groups = {View.Web.class})
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(min = 6, max = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "registered", columnDefinition = "timestamp default now()", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date registered = new Date();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant vote;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    private Set<Role> roles;

    public User(){

    }
    public User(User u){
        this(u.getId(),u.getName(), u.getEmail(), u.getPassword(), u.getRegistered(), u.getVote(), u.getRoles());
    }

    public User(Integer id, String name, String email, String password, Restaurant vote,Role role, Role...roles){
        this(id, name, email, password, new Date(), vote, EnumSet.of(role, roles));
    }

    public User(Integer id,String name, String email, String password, Date registered, Restaurant vote, Collection<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.registered = registered;
        this.vote = vote;
        setRoles(roles);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public Restaurant getVote() {
        return vote;
    }

    public void setVote(Restaurant vote) {
        this.vote = vote;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles.isEmpty() ? Collections.<Role>emptySet() : EnumSet.copyOf(roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", registered=" + registered +
                ", vote=" + vote +
                ", roles=" + roles +
                '}';
    }
}

